/**
 *
 */
package com.github.opensource21.vsynchistory.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Konfiguration für das Sceduling
 * 
 * @author niels
 *
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
        return Executors.newScheduledThreadPool(2);
    }
}
