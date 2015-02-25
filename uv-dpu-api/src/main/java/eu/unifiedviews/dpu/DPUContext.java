package eu.unifiedviews.dpu;

import java.io.File;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import eu.unifiedviews.dataunit.DataUnit;

/**
 * Context used by {@link DPU} during their execution process. The context
 * provide functions that enable DPU communicate with containment application.
 * The {@link #sendMessage(MessageType, String)} method should be used to notify user about more serious
 * events like: changing configuration, the fatal error. It is shown to user in UI.
 * The number of the massage emitted by single execution should
 * be reasonable small to preserve readability of the message log (~10 messages per execution, only main phases notice).
 * For more intensive logging please use slf4j.
 *
 * @see DPU
 */
public interface DPUContext {

    /**
     * Types of messages that can be send through the context.
     */
    enum MessageType {

        /**
         * Debug messages will be stored only if the DPU is running in debug
         * mode.
         */
        DEBUG,
        /**
         * Information messages can be used to inform about DPU execution
         * progress.
         */
        INFO,
        /**
         * Warning messages.
         */
        WARNING,
        /**
         * Error messages can be used to report non fatal error during the DPU
         * execution.
         */
        ERROR
    }

    /**
     * Send message about execution. If {@link MessageType#ERROR} message is
     * published then the execution is stopped after current DPU and the whole
     * execution failed.
     *
     * @param type Type of message.
     * @param shortMessage Short message, should not be more than 50 chars.
     */
    void sendMessage(MessageType type, String shortMessage);

    /**
     * Send message about execution. If {@link MessageType#ERROR} message is
     * published then the execution is stopped after current DPU and the whole
     * execution failed.
     *
     * @param type Type of message.
     * @param shortMessage Short message, should not be more than 50 chars.
     * @param fullMessage The full text of the message can be longer then
     * shortMessage.
     */
    void sendMessage(MessageType type,
            String shortMessage,
            String fullMessage);

    /**
     * Send message about execution. If {@link MessageType#ERROR} message is
     * published then the execution is stopped after current DPU and the whole
     * execution failed.
     *
     * @param type Type of message.
     * @param shortMessage Short message, should not be more than 50 chars.
     * @param fullMessage The full text of the message can be longer then
     * shortMessage.
     * @param exception Exception to add to the message.
     */
    void sendMessage(MessageType type,
            String shortMessage,
            String fullMessage,
            Exception exception);

    /**
     * To enable more verbose behavior of {@link DPU} execution, or more detailed information processed.
     *
     * @return True if the {@link DPU} is running in debugging mode.
     */
    boolean isDebugging();

    /**
     * Return the pipeline owner user id
     */
    String getPipelineOwner();

    /**
     * Return the pipeline owner organization
     */
    String getOrganization();

    /**
     * Return true if the execution of current {@link DPU} should be stopped as soon as
     * possible.
     *
     * @return True if the execution should stop.
     */
    boolean canceled();

    /**
     * Return path to the existing {@link DPU} working directory. The working directory
     * is unique for every {@link DPU} and execution. One can insert anything inside, directory
     * will be cleared after this particular execution.
     *
     * @return DPU's working directory.
     */
    File getWorkingDir();

    /**
     * @deprecated Do not use, will be removed in future versions. Fill data output data to {@link DataUnit}s instead.
     *
     * Return path to the existing result directory. Result directory is shared
     * by all DPU's in pipeline.
     *
     * @return Execution's result directory.
     */
    @Deprecated
    File getResultDir();

    /**
     * @deprecated Do not use, will be removed in future versions. User classloader resources if you need to load any shipped resource.
     * Return path to the jar-file which contains implementation of this DPU.
     *
     * @return Path to the this DPU's jar.
     */
    @Deprecated
    File getJarPath();

    /**
     * Return end time of last successful pipeline execution.
     *
     * @return Time or Null if there in no last execution.
     */
    Date getLastExecutionTime();

    /**
     * @deprecated Do not use, will be removed in future versions. Use {@link DPUContext#getDpuInstanceDirectory()} to store any files shared among executions.
     *
     * Return existing global DPU directory. The directory is accessible only
     * for DPU of single type (jar-file). It's shared among all the instances
     * and executions. Be aware of concurrency access when using this directory.
     *
     * @return Folder in which the DPU's are stored.
     */
    @Deprecated
    File getGlobalDirectory();

    /**
     * @deprecated Do not use, will be removed in future versions. Use {@link DPUContext#getDpuInstanceDirectory()} to store any files shared among executions.
     * Return existing DPU shared directory specific for single user. It's
     * shared among all the instances and executions for single user and certain
     * DPU (jar-file). Be aware of concurrency access when using this directory.
     *
     * @return User specific folder shared by all DPU's of single template.
     */
    @Deprecated
    File getUserDirectory();

    /**
     * Return directory URI (in form of String, ie. file://c:/Users/uv/working/dpu/324) which is unique for DPU instance (DPU use in pipeline) but shared among executions of the pipeline.
     * @return directory URI (in form of String, ie. file://c:/Users/uv/working/dpu/324)
     */
    String getDpuInstanceDirectory();

    /**
     * Return the execution environment variables
     * @return
     */
    Map<String, String> getEnvironment();

    /**
     * Return the current locale
     */
    Locale getLocale();

    /**
     * Return the current pipeline id
     */
    Long getPipelineId();

    /**
     * Return the current pipeline execution id
     */
    Long getPipelineExecutionId();

    /**
     * Return the current DPU instance id
     */
    Long getDpuInstanceId();
}
