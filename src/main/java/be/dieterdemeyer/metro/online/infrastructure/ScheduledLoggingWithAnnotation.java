package be.dieterdemeyer.metro.online.infrastructure;

import org.springframework.scheduling.annotation.Scheduled;

public class ScheduledLoggingWithAnnotation {

    @Scheduled(cron = "* * * * * ?")
    public void logSomething() {
        System.out.println("Logging something from ScheduledLoggingWithAnnotation...");
    }

}
