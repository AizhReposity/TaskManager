package com.aizh.taskmanager.task

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

abstract class Task(protected open val id: Int, protected open val name: String? = null) {
    var scheduler = Scheduler.IO
    private var taskLauncher: TaskLauncher? = null
    val dependencyTasks = mutableSetOf<Task>()
    val afterTasks = mutableSetOf<Task>()
    private val waitResultTask = ConcurrentHashMap<Int, Task>()
    private val isStarted = AtomicBoolean(false)

    fun runOn(scheduler: Scheduler) : Task {
        this.scheduler = scheduler
        return this
    }

    fun dependOn(task: Task) : Task {
        dependencyTasks.add(task)
        task.registerAfter(this)
        return this
    }

    fun beforeOn(task: Task) : Task {
        afterTasks.add(task)
        task.registerDepend(this)
        return this
    }

    private fun registerDepend(task: Task) {
        dependencyTasks.add(task)
    }

    private fun registerAfter(task: Task) {
        afterTasks.add(task)
    }

    fun prepare(taskLauncher: TaskLauncher, isReversed: Boolean) {
        this.taskLauncher = taskLauncher
        dependencyTasks.forEach { waitResultTask[it.id] = it }
        if (isReversed) {
            dependencyTasks.forEach { it.prepare(taskLauncher, isReversed) }
        } else {
            afterTasks.forEach { it.prepare(taskLauncher, isReversed) }
        }
    }

    fun onEnterTask() {
        logD("onEnterTask", "dependTasks size:${dependencyTasks.size}, afterTaskSize:${afterTasks.size}")
        if (waitResultTask.isEmpty()) {
            runningTask()
        } else {
            runDependencyTask()
        }
    }

    private fun runningTask() {
        logD("runningTask", "isStarted:${isStarted.get()}")
        if (isStarted.compareAndSet(false, true)) {
            taskLauncher?.execute(this)
        }
    }

    private fun runDependencyTask() {
        logD("runDependencyTask", "")
        dependencyTasks.forEach {
            it.onEnterTask()
        }
    }

    fun onActive() {
        logD("onActive", "")
        process()
    }

    abstract fun process()

    protected fun continueTaskChain(taskResult: TaskResult) {
        logD("continueTaskChain", "result:$taskResult")
        afterTasks.forEach { it.onDependencyTaskFinish(id, taskResult) }
    }

    protected fun finishTaskChain(reason: String) {
        logD("finishTaskChain", "reason:$reason")
        taskLauncher?.finishTaskChain(reason)
    }

    private fun onDependencyTaskFinish(id: Int, taskResult: TaskResult) {
        logD("onDependencyTaskFinish", "the finish task id:$id, taskResult:$taskResult")
        handleDependencyTaskResult(id, taskResult)
        waitResultTask.remove(id)
        if (waitResultTask.isEmpty()) {
            onEnterTask()
        }
    }

    protected open fun handleDependencyTaskResult(id: Int, taskResult: TaskResult) {

    }

    protected open fun logD(tag: String, info: Any?) {
        TaskLog.d(tag, "cur task id:$id, cur task name:$name $info")
    }

    override fun toString(): String {
        return "Task(id=$id, name=$name, scheduler=$scheduler)"
    }
}