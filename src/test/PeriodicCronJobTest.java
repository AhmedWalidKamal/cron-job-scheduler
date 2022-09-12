package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import main.job.CronJob;
import main.scheduler.JobScheduler;
import main.scheduler.JobSchedulerFactory;

public final class PeriodicCronJobTest {

    private int counter = 0;

    @Test
    void testSinglePeriodicJob() throws InterruptedException {
        JobScheduler scheduler = JobSchedulerFactory.getJobScheduler();
        scheduler.accept(new CronJob() {

            @Override
            public void run() {
                counter++;
            }

            @Override
            public Long getFrequencyInMillis() {
                return 1000L;
            }

            @Override
            public Long getExpectedRunningIntervalIfAny() {
                return null;
            }
        });

        Thread.sleep(10000);
        assertTrue(counter == 10);
    }
}
