package main;

import main.job.CronJob;
import main.job.CronJobWrapper;
import main.scheduler.JobSchedulerImpl;

public class Main {

    /**
     * Test cases:
     * 1) A single periodic task every 10 seconds.
     * 2) Two tasks each every 10 seconds in parallel.
     * 3) Having a task executing each second while having a lot of tasks being
     *    added to the queue.
     */

    public static void main(String[] args) throws Exception {
//        JobScheduler scheduler = new JobScheduler();
//        scheduler.accept(new CronJob() {
//
//            @Override
//            public void run() {
//                // job function implementation
//            }
//
//            @Override
//            public Long getFrequencyInMillis() {
//                // return the job's periodic frequency
//                return 1000L;
//            }
//
//            @Override
//            public Long getExpectedRunningIntervalIfAny() {
//                // return the job's expected running interval
//                return 1000L;
//            }
//        });
//        Thread.sleep(5000);

    }

//    private static void addJobWithDelay(JobScheduler scheduler,
//                                        CronJobWrapper job, long delay) {
//        scheduler.accept(job);
//        try {
//            Thread.sleep(delay);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static class SampleJobA extends CronJobWrapper {
//
//        public SampleJobA() {
//            super(1000);
//        }
//
//        @Override
//        public void run() {
//            System.out.println("Executing Sample Job A.");
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("Finished execution Sample Job A.");
//        }
//    }
//
//    private static class SampleJobB extends CronJobWrapper {
//
//        public SampleJobB() {
//            super(100000);
//        }
//
//        @Override
//        public void run() {
//            System.out.println("Executing Sample Job B.");
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("Finished execution Sample Job B.");
//        }
//    }
}
