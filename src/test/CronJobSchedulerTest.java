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

    @Test
    void testSinglePeriodicJob() throws InterruptedException {
        scheduler.accept(getIncrementCounterJob(1000L, null));

        Thread.sleep(10000);
        assertTrue(counter == 10);
    }

    @Test
    void testConcurrentJobs() throws InterruptedException {
        scheduler.accept(getIncrementCounterJob(1000L, null));
        scheduler.accept(getDecrementCounterJob(1000L, null));

        Thread.sleep(10000);
        assertTrue(counter == 0);
    }

    @Test
    void testAddingJobsWhileOthersAreRunning() throws InterruptedException {

        // rarely running
        scheduler.accept(createEmptyCronJob(10000L, 1L));

        // always running
        scheduler.accept(createEmptyCronJob(100L, 10000L));

        // add a job that increments the counter
        scheduler.accept(getIncrementCounterJob(1000L, null));

        // sleep for 5 seconds
        Thread.sleep(5000);

        // add a bunch of tasks
        scheduler.accept(createEmptyCronJob(10000L, 10000L));
        scheduler.accept(createEmptyCronJob(10000L, 10000L));
        scheduler.accept(createEmptyCronJob(10000L, 10000L));
        scheduler.accept(createEmptyCronJob(10000L, 10000L));
        scheduler.accept(createEmptyCronJob(10000L, 10000L));

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
