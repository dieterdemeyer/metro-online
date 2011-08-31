package be.dieterdemeyer.metro.online.infrastructure;

public class ScheduledLoggingWithNamespaceElement {

    public void logSomething() {
        System.out.println("Logging something from ScheduledLoggingWithNamespaceElement with fixed-delay...");
    }

    public void logSomethingFromCronTrigger() {
        System.out.println("Logging something from ScheduledLoggingWithNamespaceElement with cron trigger...");
    }

}
