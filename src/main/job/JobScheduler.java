package main.job;

import com.google.common.eventbus.EventBus;

import main.event.JobAddedEvent;

/**
 * Responsible for scheduling cron jobs periodically, manages adding new cron
 * jobs, executing ones at their required times, and keeping track of their
 * status.
 */
public final class JobScheduler {

    private final JobExecutor jobExecutorThread;
    private final EventBus eventBus;

    public JobScheduler() {
        // here I should start 2 main threads, one that accepts new jobs and
        // adds them to the queue of jobs to be executed, and another that is
        // responsible for executing the jobs in the queue

        // I think the main thread could be used as the one that accepts new jobs

        // shouldn't forget to handle the case of single run jobs

        /*
         * el ana mota5ayaelo hena hoa eny h-start new thread (momken yekon inner
         * class bey-extend Thread masalan) mas2ool 3an eno y-execute el jobs,
         * da hayb2a bey wait bel time beta3 el job el f awel elqueue, then ye2om
         * w y-execute el job, then y-sleep le7ad el job elba3daha w hakaza
         *
         * el main thread hena hayb2a bey-accept el jobs el gededa w bey-add them
         * lel queue beta3y
         *
         * fy case mohema w heya en yegely new job hat-get executed abl awel job
         * kanet 3andy fel queue, sa3et-ha lazem a-notify el sleeping thread beta3
         * el execution a2awemo, w a5aleh y-sleep bel time elgedeed beta3 el new
         * added job (da flow elmafrood yeb2a dayman 3ala ay new added job).
         *
         * kol job hat-get executed f thread lewa7daha spawned men execution thread,
         * w kol thread men dol mas2ool 3an recording el time of execution of the
         * job, then it could delegate this info to a logger which should handle
         * logging all info (execution time, output, etc..) of any job that ran
         * in a file.
         *
         * 3ayez afakar bardo fel case beta3et en 2 jobs 5alaso execution ma3 ba3d
         * w 3ayzeen ye3melo re-insert lel job dy bel frequency beta3et-ha fel
         * queue of jobs to be executed.
         *
         * haykoon 3andy path lel logging directory, w haykoon kol job leha el
         * file beta3ha w esmo feh el id beta3 el job.
         */

        this.eventBus = new EventBus();
        this.jobExecutorThread = new JobExecutor(this.eventBus);
        this.jobExecutorThread.start();
    }

    /**
     * Accepts a new {@link CronJob} instance, adds it to the queue of jobs to
     * be executed.
     */
    public void accept(CronJob cronJob) {
//        cronJobQueue.add(cronJob);

        System.out.println("Accepting new job with ID = " + cronJob.getId());
        eventBus.post(new JobAddedEvent(cronJob));
    }
}
