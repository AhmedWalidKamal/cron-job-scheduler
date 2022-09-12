package main.job;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.eventbus.EventBus;

import main.event.JobExecutedEvent;

/**
 * Executes the jobs by managing a thread pool, and notifies the queue manager
 * when a job finishes execution.
 */
public final class JobExecutor {

    private static final long DEFAULT_JOB_EXECUTION_INTERVAL_TIMEOUT = 10000;
    private final EventBus eventBus;

    private final ExecutorService executorService;


    public JobExecutor(EventBus eventBus) {

        this.eventBus = eventBus;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * Takes a {@link CronJob} to execute.
     */
    public void execute(CronJob jobToExecute) {
        new JobExecutionThread(jobToExecute).start();
    }

    private class JobExecutionThread extends Thread {

        private final CronJob jobToExecute;

        public JobExecutionThread(CronJob jobToExecute) {
            this.jobToExecute = jobToExecute;
        }

        @Override
        public void run() {
            Future<?> future = executorService.submit(jobToExecute);
            while (!future.isDone()) {
                try {
                    long timeout = jobToExecute.getExpectedRunningIntervalIfAny() == null
                        ? DEFAULT_JOB_EXECUTION_INTERVAL_TIMEOUT
                        : jobToExecute.getExpectedRunningIntervalIfAny();
                    future.get(timeout, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }

            // task finished execution
            eventBus.post(new JobExecutedEvent(jobToExecute));
        }
    }
}
