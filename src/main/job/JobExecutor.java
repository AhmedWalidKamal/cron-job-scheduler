package main.job;

import java.time.Instant;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import main.event.JobAddedEvent;


public class JobExecutor extends Thread {

    private final Queue<CronJob> cronJobQueue;

    private final EventBus eventBus;

    private final ExecutorService executorService;

    public JobExecutor(EventBus eventBus) {
        this.eventBus = eventBus;
        this.executorService = Executors.newFixedThreadPool(10);

        this.eventBus.register(new CronJobAddedEventListener());

        this.cronJobQueue = new PriorityBlockingQueue<CronJob>
            (11, new Comparator<CronJob>() {
                @Override
                public int compare(CronJob firstJob, CronJob secondJob) {
                    // here i should return the lastExecutedTime + frequency
                    // which is the time to execute this job
                    // this way, the queue will be sorted on the next time
                    // to execute the job
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
    }

    public void run() {
        // TODO: this thread should be sleeping until:
        // 1) A job is scheduled to be executed now
        // 2) A new job is to be added to the queue
        // 3) A job has finished execution and is ready to be re-added to the
        //    queue again to be scheduled for its next execution

        while (true) {
            synchronized(cronJobQueue) {
                while (cronJobQueue.isEmpty()) {
                    try {
                        System.out.println("waiting since the queue is empty...");
                        cronJobQueue.wait();
                    }
                    catch (InterruptedException e) {
                        System.out.println("Interrupted exception while waiting on queue: ");
                        e.printStackTrace();
                    }
                    System.out.println("Interrupted by an add event!");
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
                System.out.println("Executing the job nowwww!");
                Future<?> future = executorService.submit(cronJobQueue.poll());
            }



            // need to check here if this was waken up because the timeout
            // has elapsed, or because of a notify from an event and act
            // accordingly

//            nextJobToBeExecuted = cronJobQueue.poll();

            // spawn a thread to execute the job using executor service

        }
    }

    private class CronJobAddedEventListener {

        @Subscribe
        public void cronJobAdded(JobAddedEvent jobAddedEvent) {
            CronJob newCronJob = jobAddedEvent.getCronJob();
            System.out.println("Job with ID = " + newCronJob.getId()
                               + " has been received.");

            System.out.println("Notifying the current thread");
            synchronized(cronJobQueue) {
                cronJobQueue.add(newCronJob);
                cronJobQueue.notifyAll();
            }
        }
    }

    private class CronJobExecutedEventListener {

    }
}
