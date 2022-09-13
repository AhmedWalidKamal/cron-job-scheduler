package main.scheduler;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

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

    // add 10 seconds allowance before the job executing times-out.
    private static final long JOB_EXECUTION_TIMEOUT_ALLOWANCE_IN_MS
        = 60 * 1000;

    // a limit on the number of threads to be spawned at the same time for
    // jobs to be executed, after this limit is reached, jobs will have to wait
    // until a running job is finished and a thread from the thread-pool is
    // available for it to execute
    private static final int MAX_NUMBER_OF_CONCURRENT_JOBS = 2000;

    private final EventBus eventBus;
    private final ExecutorService executorService;

    public JobExecutor(EventBus eventBus) {
        if (eventBus == null)
            throw new IllegalArgumentException();

        this.eventBus = eventBus;
        this.executorService
            = Executors.newFixedThreadPool(MAX_NUMBER_OF_CONCURRENT_JOBS);
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
                    future.get(timeout + JOB_EXECUTION_TIMEOUT_ALLOWANCE_IN_MS,
                               TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    JobSchedulerImpl.getLogger().log
                        (Level.SEVERE,
                         "Job with ID = " + jobToExecute.getId()
                         + " is interrupted.", e);
                } catch (ExecutionException e) {
                    JobSchedulerImpl.getLogger().log
                        (Level.SEVERE,
                         "Job with ID = " + jobToExecute.getId()
                         + " failed to execute.", e);
                } catch (TimeoutException e) {
                    JobSchedulerImpl.getLogger().log
                        (Level.SEVERE,
                         "Job with ID = " + jobToExecute.getId()
                         + " has timed-out.", e);
                }
            }

            // task finished execution
            eventBus.post(new JobExecutedEvent(jobToExecute));
        }
    }
}
