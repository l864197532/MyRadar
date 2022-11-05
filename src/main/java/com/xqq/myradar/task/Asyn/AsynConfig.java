package com.xqq.myradar.task.Asyn;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

//异步线程池配置
@Configuration
@EnableAsync
public class AsynConfig implements AsyncConfigurer {

        @Override
        public Executor getAsyncExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            int cpuNum = Runtime.getRuntime().availableProcessors();
            executor.setCorePoolSize(cpuNum);
            executor.setMaxPoolSize(cpuNum*2);
            executor.setQueueCapacity(500);
            executor.initialize();
            return executor;
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return null;
        }

}

