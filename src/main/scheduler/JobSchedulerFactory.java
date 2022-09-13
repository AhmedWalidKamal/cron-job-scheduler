package main.scheduler;

/**
 * A factory responsible for creating concrete {@link JobScheduler} instances.
 * Different scheduler implementation can be added (they should implement the
 * {@link JobScheduler} interface) and returned by this factory class to the
 * client.
 */
public final class JobSchedulerFactory {

    /**
     * Returns the default {@link JobScheduler} implementation.
     */
    public static JobScheduler getJobScheduler() {
        return new JobSchedulerImpl();
    }
}
