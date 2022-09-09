package main;

import java.time.Instant;

/**
 * Represents a job that gets scheduled to be executed perodically.
 */
public abstract class CronJob implements Runnable {

    private final boolean isSingleRun;
    private final long frequencyInMillis;
    private final long id;

    /*
     * Indicates the timestamp of the last time this job was executed, if this
     * job hasn't been executed yet, then it has the value of the time of its
     * creation.
     */
    private long lastExecutedTimestamp;

    public CronJob(long frequencyInMillis, boolean isSingleRun) {
        this.frequencyInMillis = frequencyInMillis;
        this.isSingleRun = isSingleRun;
        this.id = 1; // TODO: need to use a unique ID generator here

        this.lastExecutedTimestamp = Instant.now().toEpochMilli();
    }

    public abstract void run();

    public boolean getIsSingleRun() {
        return isSingleRun;
    }

    public long getFrequencyInMillis() {
        return frequencyInMillis;
    }

    public long getId() {
        return id;
    }

    public long getLastExecutedTimestamp() {
        return lastExecutedTimestamp;
    }

    public void updateLastExecutedTimestamp() {
        lastExecutedTimestamp = Instant.now().toEpochMilli();
    }
}
