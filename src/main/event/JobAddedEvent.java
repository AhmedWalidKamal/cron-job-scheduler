package main.event;

import main.job.CronJob;

/**
 * An event that indicates that a cron job has just been added to the queue to
 * be scheduled for execution.
 */
public final class JobAddedEvent {
    private final CronJob cronJob;

    public JobAddedEvent(CronJob cronJob) {
        this.cronJob = cronJob;
    }

    public CronJob getCronJob() {
        return cronJob;
    }
}
