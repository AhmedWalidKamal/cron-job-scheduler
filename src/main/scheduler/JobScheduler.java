package main.scheduler;

import main.job.CronJob;

/**
 * An interface for a {@link CronJob} scheduler which is responsible for
 * managing and scheduling cron jobs.
 */
public interface JobScheduler {

    /**
     * Accepts a new {@link CronJob} to be scheduled for periodic execution.
     */
    void accept(CronJob cronJob);
}
