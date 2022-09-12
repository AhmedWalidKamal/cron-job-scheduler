package main.job;

import java.time.Instant;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import main.event.JobAddedEvent;
import main.event.JobExecutedEvent;

/**
 * Manages the job queue by handling events to add jobs to the queue, execute
 * jobs that are schedule to execute, and wait until a job is ready to exceute.
 */
public class JobQueueManager extends Thread {

    private final EventBus eventBus;

    private final Queue<CronJob> cronJobQueue;

    private final JobExecutor jobExecutor;

    public JobQueueManager(EventBus eventBus) {
        this.eventBus = eventBus;
        registerEvents();

        this.cronJobQueue = new PriorityBlockingQueue<CronJob>
            (11, new Comparator<CronJob>() {
                @Override
                public int compare(CronJob firstJob, CronJob secondJob) {
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
                        cronJobQueue.wait();
                    }
                    catch (InterruptedException e) {
                        System.out.println("Interrupted exception while waiting on queue: ");
                        e.printStackTrace();
                    }
                }

                CronJob nextJobToBeExecuted = cronJobQueue.peek();
                long timeToExecuteNextJob
                    = nextJobToBeExecuted.getLastExecutedTimestamp()
                    + nextJobToBeExecuted.getFrequencyInMillis();
                try {
                    cronJobQueue.wait(timeToExecuteNextJob - Instant.now().toEpochMilli());
                } catch (InterruptedException e) {
                    System.out.println("Interrupted exception while waiting on job time: ");
                    e.printStackTrace();
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
            CronJob newCronJob = jobAddedEvent.getCronJob();
            System.out.println("Job with ID = " + newCronJob.getId()
                               + " has been received to be added.");

            System.out.println("Notifying the current thread");
            synchronized(cronJobQueue) {
                cronJobQueue.add(newCronJob);
                cronJobQueue.notifyAll();
            }
        }
    }

    private class CronJobExecutedEventListener {

        @Subscribe
        public void cronJobExecuted(JobExecutedEvent jobExecutedEvent) {
            CronJob executedJob = jobExecutedEvent.getCronJob();
            System.out.println("Job with ID = " + executedJob.getId()
                               + " has finished execution.");
            executedJob.updateLastExecutedTimestamp();

            synchronized (cronJobQueue) {
                cronJobQueue.add(executedJob);
                cronJobQueue.notifyAll();
            }
        }
    }
}