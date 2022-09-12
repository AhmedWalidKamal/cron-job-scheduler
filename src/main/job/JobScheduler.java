package main.job;

import java.io.IOException;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

import main.event.JobAddedEvent;

/**
 * Responsible for scheduling cron jobs periodically, accepts new jobs that
 * will be added to the job queue to be scheduled for execution.
 */
public final class JobScheduler {

    private final EventBus eventBus;

    private static final Logger logger
        = Logger.getLogger(JobScheduler.class.getName());

    public JobScheduler() {
        this.eventBus = new EventBus();

        initializeLogger();

        // start job queue manager thread
        new JobQueueManager(this.eventBus).start();
    }

    /**
     * Accepts a new {@link CronJob} instance, adds it to the queue of jobs to
     * be executed.
     */
    public void accept(CronJob cronJob) {
        CronJobWrapper cronJobWrapper = new CronJobWrapper(cronJob);
        logger.log(Level.INFO, "Accepting new job with ID = "
                               + cronJobWrapper.getId());
        eventBus.post(new JobAddedEvent(cronJobWrapper));
    }

    public static Logger getLogger() {
        return logger;
    }

    private void initializeLogger() {
        // setting logging level
        logger.setLevel(Level.FINE);

        // adding console handler
        logger.addHandler(new ConsoleHandler());

        // adding file handler
        try {
            Handler fileHandler = new FileHandler("scheduler.log");
            fileHandler.setFormatter(new SchedulerLogFileFormatter());
            logger.addHandler(fileHandler);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A formatter to specify the format of the logs in the cron job scheduler log
     * files.
     */
    private final class SchedulerLogFileFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            return record.getThreadID() + "::" + record.getSourceClassName()
                + "::" + record.getSourceMethodName() + "::"
                + new Date(record.getMillis()) + "::" + record.getMessage()
                + "\n";
        }

    }
}
