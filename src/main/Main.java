package main;

import main.job.CronJob;
import main.job.JobScheduler;

public class Main {

    public static void main(String[] args) {
        JobScheduler scheduler = new JobScheduler();
        scheduler.accept(new Main.TestJob());
    }

    private static class TestJob extends CronJob {

        public TestJob() {
            super(100, true, 4);
        }

        @Override
        public void run() {
            System.out.println("Executing test job's run now.");
        }
    }
}
