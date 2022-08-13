package com.aizh.taskmanager

import com.aizh.taskmanager.task.Task
import com.aizh.taskmanager.task.TaskResult
import kotlin.random.Random

class DelayTask(id: Int, name: String) : Task(id, name) {

    companion object {
        const val TAG = "CountTask"
    }

    override fun process() {
        val sleep = Random(System.currentTimeMillis()).nextInt(5) * 1000L
        logD("process", "process sleep:$sleep")
        Thread.sleep(sleep)
        val result = TaskResult(5, 5)
        continueTaskChain(result)
    }
}