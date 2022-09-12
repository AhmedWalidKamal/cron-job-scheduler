package main.job;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a job that gets scheduled to be executed perodically.
 */
public abstract class CronJob implements Runnable {

    private final Long expectedRunningIntervalIfAny;
    private final long frequencyInMillis;
    private final String id;

    /*
     * Indicates the timestamp of the last time this job was executed, if this
     * job hasn't been executed yet, then it has the value of the time of its
     * creation.
     */
    private long lastExecutedTimestamp;

    public CronJob(long frequencyInMillis) {
        this(null/*expectedRunningIntervalIfAny*/, frequencyInMillis);
    }

    public CronJob(Long expectedRunningIntervalIfAny, long frequencyInMillis) {
        this(expectedRunningIntervalIfAny, frequencyInMillis,
             UUID.randomUUID().toString());
    }

    private CronJob(Long expectedRunningIntervalIfAny, long frequencyInMillis,
                    String id) {

        this.expectedRunningIntervalIfAny = expectedRunningIntervalIfAny;
        this.frequencyInMillis = frequencyInMillis;
        this.id = id;

        this.lastExecutedTimestamp = Instant.now().toEpochMilli();
    }

    public abstract void run();

    public Long getExpectedRunningIntervalIfAny() {
        return expectedRunningIntervalIfAny;
    }

    public long getFrequencyInMillis() {
        return frequencyInMillis;
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
