package com.aizh.taskmanager.task

data class TaskResult(val code: Int, val value: Any?) {
    companion object {
        const val SUCCESS = 200
        const val ERROR_KNOWN = -1
    }

    fun isSuccess() : Boolean {
        return code == SUCCESS
    }

    override fun toString(): String {
        return "TaskResult(code=$code, value=$value)"
    }
}