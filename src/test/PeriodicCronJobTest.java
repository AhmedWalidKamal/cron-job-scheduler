package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import main.job.CronJob;
import main.job.JobScheduler;

public final class PeriodicCronJobTest {

    private int counter = 1;

    @Test
    void testSinglePeriodicJob() {
        JobScheduler scheduler = new JobScheduler();
        scheduler.accept(new IncrementCounterJob());

        // sleep for 10 seconds
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(counter);
        assertTrue(counter == 10);
    }

    @Test
    void testMultiplePeriodicJobs() {
        JobScheduler scheduler = new JobScheduler();
        scheduler.accept(new IncrementCounterJob());

        // sleep for 10 seconds
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(counter);
        assertTrue(counter == 10);
    }

    /**
     * A periodic job that runs each second and just increments a counter.
     */
    private final class IncrementCounterJob extends CronJob {

        public IncrementCounterJob() {
            super(1000);
        }

        @Override
        public void run() {
            counter++;
        }
    }

}
