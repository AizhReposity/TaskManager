package com.aizh.taskmanager.task

import android.util.Log

object TaskLog {
    const val TAG = "TaskLog"

    fun d(tag: String, info: String) {
        Log.v("${TAG}_${tag}", info)
    }

    fun w(tag: String, info: String) {
        Log.w("${TAG}_${tag}", info)
    }

    fun e(tag: String, info: String) {
        Log.e("${TAG}_${tag}", info)
    }
}