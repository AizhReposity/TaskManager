package com.aizh.taskmanager.task

import kotlinx.coroutines.*
import java.util.*

/**
 * @param attachTask 任务头或任务尾
 * @param isReverse 是否从任务尾触发
 */
class TaskLauncher(private val attachTask : Task, private val isReverse: Boolean) {

    companion object {
        const val TAG = "TaskLauncher"
    }

    private val scope = CoroutineScope(SupervisorJob() + CoroutineExceptionHandler { coroutineContext, throwable ->
        TaskLog.d(TAG, throwable.toString())
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

    /**
     * DFS + 回溯查询
     */
    private fun checkCircleDependency(task: Task, isReverse: Boolean, queue: LinkedList<Task>) : Boolean {
        if (queue.contains(task)) {
            queue.add(task)
            TaskLog.e("checkCircleDependency", "环形依赖: $queue")
            return true
        }
        queue.add(task)
        val checkList = if (isReverse) task.dependencyTasks else task.afterTasks
        checkList.forEach {
            val result = checkCircleDependency(it, isReverse, queue)
            if (result) {
                return true
            }
        }
        queue.remove(task)
        return false
    }

    fun start() {
        scope.launch {
            if (!checkCircleDependency(attachTask, isReverse, LinkedList<Task>())) {
                attachTask.prepare(this@TaskLauncher, isReverse)
                attachTask.onEnterTask()
                TaskLog.d(TAG, "start task success")
            } else {
                TaskLog.e(TAG, "start task failed because circle dependency")
            }
        }
    }
}