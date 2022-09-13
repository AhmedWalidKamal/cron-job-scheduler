package main;

/**
 * Represents a job that gets scheduled to be executed perodically.
 */
public abstract class CronJob implements Runnable {

    private final boolean isSingleRun;
    private final long frequencyInMillis;
    private final long id;

    public CronJob(long frequencyInMillis, boolean isSingleRun) {
        this.frequencyInMillis = frequencyInMillis;
        this.isSingleRun = isSingleRun;
        this.id = 1;
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
}
