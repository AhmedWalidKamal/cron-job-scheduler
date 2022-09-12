package main.job;

/**
 * A Cron Job interface used by the client to specify:
 *   1) The job implementation to schedule for execution.
 *   2) The expected interval for a single run for this job.
 *   3) The scheduling frequency.
 */
public interface CronJob extends Runnable {

    /**
     * @return {@link Long} the frequency by which to run this job
     * in milliseconds.
     */
    Long getFrequencyInMillis();

    /**
     * @return {@link Long} the expected running interval of this job in
     * milliseconds, or null if no expected interval is specified.
     */
    Long getExpectedRunningIntervalIfAny();
}
