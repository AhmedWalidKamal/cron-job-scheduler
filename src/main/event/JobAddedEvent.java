package main.event;

import main.job.CronJobWrapper;

/**
 * An event that indicates that {@link CronJob} has just been added to the queue
 * to be scheduled for execution.
 */
public final class JobAddedEvent {
    private final CronJobWrapper cronJobWrapper;

    public JobAddedEvent(CronJobWrapper cronJobWrapper) {
        this.cronJobWrapper = cronJobWrapper;
    }

    public CronJobWrapper getCronJobWrapper() {
        return cronJobWrapper;
    }
}
