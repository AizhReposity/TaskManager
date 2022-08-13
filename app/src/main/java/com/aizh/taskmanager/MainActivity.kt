package com.aizh.taskmanager

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import com.aizh.taskmanager.task.Scheduler
import com.aizh.taskmanager.task.TaskManager
import com.example.aizhkotlindemo.R

class MainActivity : AppCompatActivity(), LifecycleObserver {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tv = findViewById<TextView>(R.id.tv)
        tv.setOnClickListener {
            testTask()
        }
    }

    private fun testTask() {
        val task1 = DelayTask(1, "任务1").runOn(Scheduler.MAIN)
        val task2 = DelayTask(2, "任务2").runOn(Scheduler.IO)
        val task3 = DelayTask(3, "任务3").runOn(Scheduler.IO)
        val task4 = DelayTask(4, "任务4").runOn(Scheduler.IO)
        val task5 = DelayTask(5, "任务5").runOn(Scheduler.IO)
        val task6 = DelayTask(6, "任务6").runOn(Scheduler.IO)

        task1.dependOn(task2)
        task1.dependOn(task3)
        task2.dependOn(task4)
        task2.dependOn(task5)
        task3.dependOn(task6)
        TaskManager(task1, true).start()
    }

}