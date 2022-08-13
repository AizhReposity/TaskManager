package com.aizh.taskmanager.task

import kotlinx.coroutines.*
import java.util.*

/**
 * @param attachTask 任务头或任务尾
 * @param isReverse 是否从任务尾触发
 */
class TaskManager(private val attachTask : Task, private val isReverse: Boolean) {

    companion object {
        const val TAG = "TaskContext"
    }

    private val scope = CoroutineScope(SupervisorJob() + CoroutineExceptionHandler { coroutineContext, throwable ->
        TaskLog.d(TAG, throwable.message.toString())
    })

    fun execute(task: Task) {
        if (task.scheduler == Scheduler.MAIN) {
            scope.launch(Dispatchers.Main) {
                task.onActive()
            }
        } else {
            scope.launch(Dispatchers.IO) {
                task.onActive()
            }
        }
    }

    fun finishTaskChain(reason: String) {
        TaskLog.e(TAG, "存在环形依赖，启动任务失败")
    }

    private fun checkCircleDependency() : Boolean {
        val levelMap = mutableMapOf<Task, Int>()
        var level = 0
        val curLevelTask = ArrayDeque<Task>()
        val nextLevelTask = ArrayDeque<Task>()
        curLevelTask.offer(attachTask)
        while (curLevelTask.isNotEmpty()) {
            val task = curLevelTask.poll()
            if (levelMap.containsKey(task) && levelMap[task]!! < level) {
                return true
            }
            levelMap[task] = level
            nextLevelTask.addAll(if (isReverse) task.dependencyTasks else task.afterTasks)
            if (curLevelTask.isEmpty()) {
                level++
                curLevelTask.addAll(nextLevelTask)
                nextLevelTask.clear()
            }
        }
        return false
    }

    fun start() {
        scope.launch {
            if (!checkCircleDependency()) {
                attachTask.prepare(this@TaskManager, isReverse)
                attachTask.onEnterTask()
                TaskLog.d(TAG, "start task success")
            } else {
                TaskLog.e(TAG, "start task failed because circle dependency")
            }
        }
    }
}