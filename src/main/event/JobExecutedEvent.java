package main.event;

import main.job.CronJobWrapper;

/**
 * An event that indicates that a {@link CronJob} has finished execution.
 */
public final class JobExecutedEvent {
    private final CronJobWrapper cronJobWrapper;

    public JobExecutedEvent(CronJobWrapper cronJobWrapper) {
        this.cronJobWrapper = cronJobWrapper;
    }

    public CronJobWrapper getCronJobWrapper() {
        return cronJobWrapper;
    }
}
