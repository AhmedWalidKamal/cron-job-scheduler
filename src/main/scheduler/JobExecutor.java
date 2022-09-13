package main.scheduler;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.eventbus.EventBus;

import main.event.JobExecutedEvent;
import main.job.CronJobWrapper;

/**
 * Executes the jobs by managing a thread pool, and notifies the queue manager
 * when a job finishes execution.
 */
public final class JobExecutor {

    // default job execution time is around 10 mins
    private static final long DEFAULT_JOB_EXECUTION_INTERVAL_TIMEOUT
        = 10 * 60 * 1000;
    private final EventBus eventBus;

    private final ExecutorService executorService;

    public JobExecutor(EventBus eventBus) {
        if (eventBus == null)
            throw new IllegalArgumentException();

        this.eventBus = eventBus;
        this.executorService = Executors.newFixedThreadPool(20);
    }

    /**
     * Takes a {@link CronJobWrapper} to execute.
     */
    public void execute(CronJobWrapper jobToExecute) {
        new JobExecutionThread(jobToExecute).start();
    }

    private class JobExecutionThread extends Thread {

        private final CronJobWrapper jobToExecute;

        public JobExecutionThread(CronJobWrapper jobToExecute) {
            this.jobToExecute = jobToExecute;
        }

        @Override
        public void run() {
            Future<?> future = executorService.submit(jobToExecute.getCronJob());
            while (!future.isDone()) {
                try {
                    long timeout
                        = jobToExecute.getExpectedRunningIntervalIfAny() == null
                        ? DEFAULT_JOB_EXECUTION_INTERVAL_TIMEOUT
                        : jobToExecute.getExpectedRunningIntervalIfAny();

                    // block till job finishes execution, or times out
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
