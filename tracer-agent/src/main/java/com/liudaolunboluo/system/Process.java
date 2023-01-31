package com.liudaolunboluo.system;



import java.util.Date;

/**
 * A process managed by the shell.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public interface Process {
    /**
     * @return the current process status
     */
    ExecStatus status();

    /**
     * @return the process exit code when the status is {@link ExecStatus#TERMINATED} otherwise {@code null}
     */
    Integer exitCode();



    /**
     * Run the process.
     */
    void run();

    /**
     * Run the process.
     */
    void run(boolean foreground);

    /**
     * Attempt to interrupt the process.
     *
     * @return true if the process caught the signal
     */
    boolean interrupt();


    /**
     * Suspend the process.
     */
    void resume();

    /**
     * Suspend the process.
     */
    void resume(boolean foreground);


    /**
     * Resume the process.
     */
    void suspend();

    /**
     * Terminate the process.
     */
    void terminate();

    /**
     * Set the process in background.
     */
    void toBackground();


    /**
     * Set the process in foreground.
     */
    void toForeground();


    /**
     * Execution times
     */
    int times();

    /**
     * Build time
     */
    Date startTime();

    /**
     * Get cache file location
     */
    String cacheLocation();

    /**
     * Set job id
     * 
     * @param jobId job id
     */
    void setJobId(int jobId);
}
