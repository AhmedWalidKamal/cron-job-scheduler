package main;

import main.job.CronJob;
import main.job.JobScheduler;

public class Main {

    /**
     * Test cases:
     * 1) A single periodic task every 10 seconds.
     * 2) Two tasks each every 10 seconds in parallel.
     * 3) Having a task executing each second while having a lot of tasks being
     *    added to the queue.
     */

    public static void main(String[] args) throws Exception {
        JobScheduler scheduler = new JobScheduler();
        Thread.sleep(5000);
        scheduler.accept(new Main.TestJob());
    }

    private static class TestJob extends CronJob {

        public TestJob() {
            super(10000);
        }

        @Override
        public void run() {
            System.out.println("Executing test job's run now.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Finished execution test job.");
        }
    }
}
