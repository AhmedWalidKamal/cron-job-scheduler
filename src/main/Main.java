package main;

import main.job.CronJob;
import main.job.JobScheduler;

public class Main {

    public static void main(String[] args) throws Exception {
        JobScheduler scheduler = new JobScheduler();
        Thread.sleep(10000);
        scheduler.accept(new Main.TestJob());
        scheduler.accept(new Main.TestJob());
        scheduler.accept(new Main.TestJob());
        scheduler.accept(new Main.TestJob());
        Thread.sleep(1000);
        scheduler.accept(new Main.TestJob());
        scheduler.accept(new Main.TestJob());
        scheduler.accept(new Main.TestJob());
        scheduler.accept(new Main.TestJob());
        scheduler.accept(new Main.TestJob());
        scheduler.accept(new Main.TestJob());
        scheduler.accept(new Main.TestJob());
        scheduler.accept(new Main.TestJob());
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
