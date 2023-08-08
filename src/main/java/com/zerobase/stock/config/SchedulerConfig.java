package com.zerobase.stock.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();

        int n = Runtime.getRuntime().availableProcessors();
        threadPool.setPoolSize(n);
        // cpu 처리가 많은 경우
//        threadPool.setPoolSize(n + 1);
        // I/O 작업이 많은 경우
//        threadPool.setPoolSize(n * 2);
        threadPool.initialize();

        taskRegistrar.setTaskScheduler(threadPool);
    }
}
