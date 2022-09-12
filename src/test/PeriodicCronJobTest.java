package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import main.job.CronJob;
import main.job.CronJobWrapper;
import main.job.JobScheduler;

public final class PeriodicCronJobTest {

    private int counter = 1;

    @Test
    void testSinglePeriodicJob() {
        JobScheduler scheduler = new JobScheduler();
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

        // sleep for 10 seconds
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(counter);
        assertTrue(counter == 10);
    }
}
