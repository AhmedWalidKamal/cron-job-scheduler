# cron-job-scheduler

A scheduler which is responsible for accepting new cron jobs, and executing them perodically according to their specified frequency.\
For the scheduled cron jobs, clients specify:

* A single run expected interval in milliseconds. (**optional**)
* Scheduling frequency in milliseconds, e.g. `1000ms` for a job that should run every second. (**required**)
* The job implementation (function). (**required**)

The scheduler is developed using Java.

## Solution Summary
### Entities
The client is exposed to 2 interfaces:

* **The `CronJob` interface**: an interface that the client should implement to specify the properties of a cron job, the methods needed to be implemented specify the job's expected interval, the scheduling frequency, and a the job implementation.
* **The `JobScheduler` interface**: this is the API by which the client can use to execute different queries for the scheduler, currently it only has an `accept(CronJob)` method by which the client can use to add jobs to be scheduled for execution. This API can contain other queries to remove a job from the queue, get a list of the current jobs and their status, etc.. \

Events are used for synchronization and communication between threads to control the Jobs Queue. Currently supported events:
* `JobAddedEvent`: that indicates that a new cron job is to be added to the queue for execution.
* `JobExecutedEvent`: that indicates that a cron job has finished execution and is ready to be scheduled for execution again after its specified time period elapses.

A `JobExecutor` is responsible for handling jobs by managing a thread pool that is used to execute each job that needs to be executed (allows for concurrent execution of jobs). 

A `JobQueueManager` is responsible for creating a queue for the jobs, this queue is always sorted with the jobs that should be executed first. Its main thread manages the queue as follows:
 * If the queue is empty, simply wait until another thread notifies with a change in the queue.
 * If the queue is not empty, check if the nearest time for the closest job to be executed, if we already reached this time, then use the `JobExecutor` to spawn a new thread to execute this job, if we haven't reached this time, then wait until this time is reached. **Note** that while this thread is waiting if a new job is added to the queue that should be executed before the first job in the queue, this thread gets notified and re-executes the logic to schedule the nearest job first.

This entity is also responsible for handling `JobAddedEvent`s and `JobExecutedEvent`s by notifying waiting threads on the queue.

### Execution Flow
When the scheduler is first initialized, 2 main threads are started:
* One thread (**The scheduler thread**) that is responsible for accepting queries from the client, e.g. accepting a new job.
* One thread (**The queue manager thread**) that is responsible for handling and managing the job queue as previously described.

When the client adds a new job, the `Scheduler` posts a`JobAddedEvent` to notify the `JobQueueManager` that a new job is to be added to the queue. The `JobQueueManager`'s event handler first acquires a lock on the job queue, then adds the new job to the queue and notifies the waiting thread on the queue (**The queue manager thread**).

When a job is ready for execution, the `JobQueueManager` calls `JobExecutor.execute(CronJob)` in which the `JobExecutor` will spawn a new thread and execute the `CronJob`'s method. 

When the job finishes execution, the thread running the job posts a `JobExecutedEvent` before exiting to notify the `JobQueueManager` that this job should be re-added to the queue to be scheduled for execution at its next interval.


## Technical Decisions
* Only a `CronJob` interface is exposed to the client to hide the implementation details and allow for only some limited API to be exposed to the client.
* A `JobSchedulerFactory` is exposed to the client to allow for the creation of a `JobScheduler` instance. This way, the implementation is abstracted away from the client and this allows for extensibility and the ability to implement different schedulers that operate in different ways (are implemented differently).
* The `CronJob`'s Unique ID is generated from the system not provided by the client, as there can be separate different clients using the scheduler and they aren't aware of each other, so they can't determine if the ID they are supplying to the job is unique or not.
* The single run expected interval specified by the user is used to provide a timeout for the execution of the Job (+ some allowance latency). It's also optional and a default value is used if the user doesn't wish to provide this value.
* Java Util's `Logger` is first configured and instantiated when the `JobScheduler` first starts, it logs all information (like job execution time, added jobs, etc..), errors and debug information into a log file specifying the timestamp of the log, the class, the thread ID and the log message. 
* Guava's `Eventbus` is used as it allows publish-subscribe communication between components. Its main advantage is that it allows components using the `EventBus` to not be aware of each other, components just either `post` or `listen` on events without being coupled to other components.
* Java's `ExecutorService` is used to manage the thread pool for executing jobs. This means that there is an upper limit on the number of parallel jobs that can be executed concurrently. This is enforced to avoid running out of system resources as if there was no limit, the number of concurrent jobs could overwhelm the system's resources and the application could crash. Jobs trying to execute after reaching the limit wait until a running job finishes execution and a thread from the thread pool is available for it to execute.

## Example Usage Snippet
The following snippet illustrates how the client can add a new job to the scheduler:
```java
JobScheduler scheduler = new JobScheduler();
scheduler.accept(new CronJob() {        
    @Override
    public void run() {
        // job function implementation
    }
            
    @Override
    public Long getFrequencyInMillis() {
        // return the job's periodic frequency
        return 1000L;
    }
            
    @Override
    public Long getExpectedRunningIntervalIfAny() {
        // return the job's expected running interval
        return 1000L;
    }
});

```

## Future Improvements
* Currently the client has to specify a periodic frequency in `milliseconds` for the submitted job, further UX improvements can be applied here:
    * The client could be able to specify the frequency alongside the unit of time (`hr`, `min`, `sec`, ...).
    * The client could be able to specify a specific time 
* Logging could be improved and organized such that each job can have its own log file, where its named after the job ID to ensure uniqueness, each job log file would be responsible for logging all information about this job (when it gets executed, re-entered in the job queue, failure of execution, etc..). Also, the scheduler itself should have a log file to monitor jobs being added/removed by client queries.
* More features could be added to the `JobScheduler` API to allow for the client to have more queries like:
   * Removing a job from the scheduler.
   * Getting a list of all jobs in the scheduler and their current status.
   * Getting the log history of a specific job.
   * An option to specify/limit resources for a specific job.
