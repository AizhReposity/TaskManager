# TaskManager
Android任务管理，按任务依赖顺序执行，参考有向无环图





        val task1 = DelayTask(1, "任务1").runOn(Scheduler.MAIN)
        val task2 = DelayTask(2, "任务2").runOn(Scheduler.IO)
        val task3 = DelayTask(3, "任务3").runOn(Scheduler.IO)
        val task4 = DelayTask(4, "任务4").runOn(Scheduler.IO)
        val task5 = DelayTask(5, "任务5").runOn(Scheduler.IO)
        val task6 = DelayTask(6, "任务6").runOn(Scheduler.IO)

        //从任务尾触发，用于1尾多头
        task1.dependOn(task2)
        task1.dependOn(task3)
        task2.dependOn(task4)
        task2.dependOn(task5)
        task3.dependOn(task6)
        TaskManager(task1, true).start()
        //从任务头触发，用于1头多尾
        task1.beforeOn(task2)
        task1.beforeOn(task3)
        task2.beforeOn(task4)
        task2.beforeOn(task5)
        task3.beforeOn(task6)
        TaskManager(task1, false).start()
