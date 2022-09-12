package main.log;

import org.apache.logging.log4j.core.Logger;

import main.job.CronJob;

/**
 * Responsible for cron jobs' info in log files. Logs information like
 * start of execution time, end of execution time, any output of the job, time
 * taken to execute the job, etc...
 *
 * Each Cron Job would have a log file (all files would be in a pre-determined
 * directory path defined as a constant here) and each file would be identified
 * with the Job's ID (maybe alongside a prefix/suffix).
 */
public final class CronJobLogger {

    private final CronJob cronJob;

    public CronJobLogger(CronJob cronJob) {
        this.cronJob = cronJob;
    }
}
