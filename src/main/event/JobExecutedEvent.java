package main.event;

import main.job.CronJob;

/**
 * An event that indicates that a cron job has finished execution.
 */
public final class JobExecutedEvent {
    private final CronJob cronJob;

    public JobExecutedEvent(CronJob cronJob) {
        this.cronJob = cronJob;
    }

    public CronJob getCronJob() {
        return cronJob;
    }
}
