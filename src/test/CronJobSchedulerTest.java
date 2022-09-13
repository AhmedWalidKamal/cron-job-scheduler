package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.job.CronJob;
import main.scheduler.JobScheduler;
import main.scheduler.JobSchedulerFactory;

public final class CronJobSchedulerTest {

    private JobScheduler scheduler;

    private int counter = 0;

    @BeforeEach
    void init() {
        scheduler = JobSchedulerFactory.getJobScheduler();
        counter = 0;
    }

    /**
     * Adds a single periodic job that runs every second and increments the
     * counter, asserts that after 10 seconds, this job would have run 10 times.
     */
    @Test
    void testSinglePeriodicJob() throws InterruptedException {
        scheduler.accept(getIncrementCounterJob(1000L, null));

        Thread.sleep(10000);
        assertTrue(counter == 10);
    }

    /**
     * Adds 2 jobs that run in parallel, one increments the counter and the
     * other decrements it, both runs each second, asserts that the counter is
     * zero after some period of time.
     */
    @Test
    void testConcurrentJobs() throws InterruptedException {
        scheduler.accept(getIncrementCounterJob(1000L, null));
        scheduler.accept(getDecrementCounterJob(1000L, null));

        Thread.sleep(13000);
        assertTrue(counter == 0);
    }

    /**
     * Tests that the queue of jobs executes the jobs that are nearest in time
     * first.
     */
    @Test
    void testJobsOrder() throws InterruptedException {
        scheduler.accept(getDecrementCounterJob(60000L, 10L));
        scheduler.accept(getIncrementCounterJob(10000L, 10L));

        Thread.sleep(15000);
        assertTrue(counter == 1);
    }

    /**
     * Tests adding jobs while the queue has a lot of jobs in it.
     */
    @Test
    void testBusySchedule() throws InterruptedException {

        // this job mostly stays in the queue
        scheduler.accept(createEmptyCronJob(10000L, 1L));

        // this task is frequently running
        scheduler.accept(createEmptyCronJob(100L, 10000L));

        // add a bunch of tasks
        for (int i = 0; i < 1000; i++)
            scheduler.accept(createEmptyCronJob(10000L, 10000L));

        // add a job that increments the counter
        scheduler.accept(getIncrementCounterJob(1000L, null));

        // sleep for 5 seconds
        Thread.sleep(5000);

        // add a job that decrements the counter
        scheduler.accept(getDecrementCounterJob(1000L, null));

        // sleep for 5 more seconds
        Thread.sleep(10000);

        assertTrue(counter == 5);
    }

    private CronJob createEmptyCronJob
        (Long frequency, Long expectedRunningIntervalIfAny) {

        return new CronJob() {

            @Override
            public void run() {
                try {
                    // running interval is 1 sec by default if not specified
                    Long runningInterval = expectedRunningIntervalIfAny != null
                        ? expectedRunningIntervalIfAny
                        : 1000L;

                    Thread.sleep(runningInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public Long getFrequencyInMillis() {
                return frequency;
            }

            @Override
            public Long getExpectedRunningIntervalIfAny() {
                return expectedRunningIntervalIfAny;
            }
        };
    }

    private CronJob getIncrementCounterJob(Long frequency, Long intervalIfAny) {
        return new CronJob() {
            @Override
            public void run() {
                counter++;
            }

            @Override
            public Long getFrequencyInMillis() {
                return frequency;
            }

            @Override
            public Long getExpectedRunningIntervalIfAny() {
                return intervalIfAny;
            }
        };
    }

    private CronJob getDecrementCounterJob(Long frequency, Long intervalIfAny) {
        return new CronJob() {
            @Override
            public void run() {
                counter--;
            }

            @Override
            public Long getFrequencyInMillis() {
                return frequency;
            }

            @Override
            public Long getExpectedRunningIntervalIfAny() {
                return intervalIfAny;
            }
        };
    }
}
