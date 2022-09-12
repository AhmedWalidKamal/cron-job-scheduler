package main.job;

import java.time.Instant;
import java.util.UUID;

/**
 * A wrapper to a {@link CronJob} instance, adds some useful metadata like the
 * job's unique ID, the last executed timestamp, etc..
 */
public final class CronJobWrapper {

    private final CronJob cronJob;

    private final String id;

    /*
     * Indicates the timestamp of the last time this job was executed, if this
     * job hasn't been executed yet, then it has the value of the time of its
     * creation - the frequency of its repetition (to be executed right away).
     */
    private long lastExecutedTimestamp;

    public CronJobWrapper(CronJob cronJob) {

        this.cronJob = cronJob;
        this.id = UUID.randomUUID().toString();

        this.lastExecutedTimestamp
            = Instant.now().toEpochMilli() - cronJob.getFrequencyInMillis();
    }

    public CronJob getCronJob() {
        return cronJob;
    }

    public Long getExpectedRunningIntervalIfAny() {
        return cronJob.getExpectedRunningIntervalIfAny();
    }

    public long getFrequencyInMillis() {
        return cronJob.getFrequencyInMillis();
    }

    public String getId() {
        return id;
    }

    public long getLastExecutedTimestamp() {
        return lastExecutedTimestamp;
    }

    public void updateLastExecutedTimestamp() {
        lastExecutedTimestamp = Instant.now().toEpochMilli();
    }
}
