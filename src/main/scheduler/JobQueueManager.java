package main.scheduler;

import java.time.Instant;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import main.event.JobAddedEvent;
import main.event.JobExecutedEvent;
import main.job.CronJobWrapper;

/**
 * Manages the job queue by handling events to add jobs to the queue, execute
 * jobs that are schedule to execute, and wait until a job is ready to exceute.
 */
public final class JobQueueManager extends Thread {

    private final EventBus eventBus;

    private final Queue<CronJobWrapper> cronJobQueue;

    private final JobExecutor jobExecutor;

    public JobQueueManager(EventBus eventBus) {
        this.eventBus = eventBus;
        registerEvents();

        this.cronJobQueue = new PriorityBlockingQueue<CronJobWrapper>
            (11, new Comparator<CronJobWrapper>() {
                @Override
                public int compare
                    (CronJobWrapper firstJob, CronJobWrapper secondJob) {

                    long timeToExecuteFirstJob
                        = firstJob.getLastExecutedTimestamp()
                        + firstJob.getFrequencyInMillis();

                    long timeToExecuteSecondJob
                        = secondJob.getLastExecutedTimestamp()
                        + secondJob.getFrequencyInMillis();
                    return Long.compare
                        (timeToExecuteFirstJob, timeToExecuteSecondJob);
                }
        });

        this.jobExecutor = new JobExecutor(eventBus);
    }

    @Override
    public void run() {
        while (true) {
            synchronized(cronJobQueue) {
                while (cronJobQueue.isEmpty()) {
                    try {
                        JobSchedulerImpl.getLogger().log
                            (Level.INFO, "Waiting till a job is added");
                        cronJobQueue.wait();
                    }
                    catch (InterruptedException e) {
                        JobSchedulerImpl.getLogger().log
                            (Level.WARNING,
                             "Interrupted exception while waiting on queue: "
                             + e);
                    }
                }

                CronJobWrapper nextJobToBeExecuted = cronJobQueue.peek();
                long timeToExecuteNextJob
                    = nextJobToBeExecuted.getLastExecutedTimestamp()
                    + nextJobToBeExecuted.getFrequencyInMillis();
                while (timeToExecuteNextJob > Instant.now().toEpochMilli()) {
                    try {
                        cronJobQueue.wait
                            (timeToExecuteNextJob - Instant.now().toEpochMilli());
                    } catch (InterruptedException e) {
                        JobSchedulerImpl.getLogger().log
                            (Level.WARNING,
                             "Interrupted exception while waiting on job time: "
                             + e);
                    }

                    timeToExecuteNextJob
                        = cronJobQueue.peek().getLastExecutedTimestamp()
                        + cronJobQueue.peek().getFrequencyInMillis();
                }

                jobExecutor.execute(cronJobQueue.poll());
            }
        }
    }

    //
    // Private methods
    //

    private void registerEvents() {
        eventBus.register(new CronJobAddedEventListener());
        eventBus.register(new CronJobExecutedEventListener());
    }

    //
    // Nested Listeners
    //

    private class CronJobAddedEventListener {

        @Subscribe
        public void cronJobAdded(JobAddedEvent jobAddedEvent) {
            CronJobWrapper newCronJob = jobAddedEvent.getCronJobWrapper();

            JobSchedulerImpl.getLogger().log
                (Level.INFO, "Job with ID = " + newCronJob.getId()
                 + " has been received to be added.");

            synchronized(cronJobQueue) {
                cronJobQueue.add(newCronJob);
                cronJobQueue.notifyAll();
            }
        }
    }

    private class CronJobExecutedEventListener {

        @Subscribe
        public void cronJobExecuted(JobExecutedEvent jobExecutedEvent) {
            CronJobWrapper executedJob = jobExecutedEvent.getCronJobWrapper();
            JobSchedulerImpl.getLogger().log
                (Level.INFO, "Job with ID = " + executedJob.getId()
                 + " has finished execution. It took approximately: "
                 + (Instant.now().toEpochMilli() - executedJob.getLastExecutedTimestamp())
                 + " Milliseconds to finish.");

            executedJob.updateLastExecutedTimestamp();

            synchronized (cronJobQueue) {
                cronJobQueue.add(executedJob);
                cronJobQueue.notifyAll();
            }
        }
    }
}
