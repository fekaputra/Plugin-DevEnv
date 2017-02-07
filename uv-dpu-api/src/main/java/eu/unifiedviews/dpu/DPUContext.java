/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.unifiedviews.dpu;

import eu.unifiedviews.dataunit.DataUnit;

import java.io.File;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

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
     * @param type
     *            Type of message.
     * @param shortMessage
     *            Short message, should not be more than 50 chars.
     */
    void sendMessage(MessageType type, String shortMessage);

    /**
     * Send message about execution. If {@link MessageType#ERROR} message is
     * published then the execution is stopped after current DPU and the whole
     * execution failed.
     * 
     * @param type
     *            Type of message.
     * @param shortMessage
     *            Short message, should not be more than 50 chars.
     * @param fullMessage
     *            The full text of the message can be longer then
     *            shortMessage.
     */
    void sendMessage(MessageType type,
            String shortMessage,
            String fullMessage);

    /**
     * Send message about execution. If {@link MessageType#ERROR} message is
     * published then the execution is stopped after current DPU and the whole
     * execution failed.
     * 
     * @param type
     *            Type of message.
     * @param shortMessage
     *            Short message, should not be more than 50 chars.
     * @param fullMessage
     *            The full text of the message can be longer then
     *            shortMessage.
     * @param exception
     *            Exception to add to the message.
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
     * Get information whether the RDF performance optimization is enabled for the given {@link DataUnit}, i.e., may
     * change its data directly - this is possible when:
     * 1) the DPU is NOT executed in debug mode (because in this case we need intermediate data)
     * 2) the {@link DataUnit} of this DPU is the only {@link DataUnit} working on top of the output data produced by the preceding DPU
     * (so that input data is not accidentally changed for another parallel DPU)
     * If the DPUs may change the inputs, they may use that for further optimizations - e.g. SPARQL Update
     * DPU does not need to copy initial data but works directly on top of input data
     * It makes sense to call such method on the input {@link DataUnit}s.
     *
     * @param dataunit
     *            {@link DataUnit} which should be tested whether it can be optimized
     * @return True if RDF performance may be optimized for the {@link DataUnit}
     */
     boolean isPerformanceOptimizationEnabled(DataUnit dataunit);

    /**
     * Return pipeline owner user name
     * 
     * @return Pipeline owner user name
     */
    String getPipelineOwner();

    /**
     * Return the user name of user who started or scheduled the pipeline execution
     * 
     * @return Pipeline execution owner name
     */
    String getPipelineExecutionOwner();

    /**
     * Return the external Id of user who started or scheduled the pipeline execution
     * 
     * @return Pipeline execution owner external Id
     */
    String getPipelineExecutionOwnerExternalId();

    /**
     * If pipeline executing user has meta data (actor), returns actor external Id
     * 
     * @return actor id if present, null otherwise
     */
    String getPipelineExecutionActorExternalId();

    /**
     * @deprecated Organization concept has been removed from UV and was replaced by actor concept; To provide
     *             backward compatibility, this method will be preserved but should not be used anymore as there can
     *             only be a dummy implementation of this method (e.g. empty String)
     * @return name of organization
     */
    @Deprecated
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
     *             Return path to the existing result directory. Result directory is shared
     *             by all DPU's in pipeline.
     * @return Execution's result directory.
     */
    @Deprecated
    File getResultDir();

    /**
     * @deprecated Do not use, will be removed in future versions. User classloader resources if you need to load any shipped resource.
     *             Return path to the jar-file which contains implementation of this DPU.
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
     * @deprecated Sharing any files among executions goes against the idea of execution of pipelines using pool of backends.
     *             Return existing global DPU directory. The directory is accessible only
     *             for DPU of single type (jar-file). It's shared among all the instances
     *             and executions. Be aware of concurrency access when using this directory.
     * @return Folder in which the DPU's are stored.
     */
    @Deprecated
    File getGlobalDirectory();

    /**
     * @deprecated Sharing any files among executions goes against the idea of execution of pipelines using pool of backends.
     *             Return existing DPU shared directory specific for single user. It's
     *             shared among all the instances and executions for single user and certain
     *             DPU (jar-file). Be aware of concurrency access when using this directory.
     * @return User specific folder shared by all DPU's of single template.
     */
    @Deprecated
    File getUserDirectory();

    /**
     * @deprecated Sharing any files among executions goes against the idea of execution of pipelines using pool of backends.
     *             Return directory URI (in form of String, ie. file://c:/Users/uv/working/dpu/324) which is unique for DPU instance (DPU use in pipeline) but
     *             shared among
     *             executions of the pipeline.
     * @return directory URI (in form of String, ie. file://c:/Users/uv/working/dpu/324)
     */
    @Deprecated
    String getDpuInstanceDirectory();

    /**
     * Return the execution environment variables.
     *
     * Execution environment variables are collected from config.properties file properties and from runtime properties.
     *
     * It may be used to e.g. set up loader correspondingly based on
     * the particular deployment (test, pre-release, release).
     *
     * Such map SHOULD NOT be used for exchanging data between DPUs.
     *
     * @return Map of environment variables.
     */
    Map<String, String> getEnvironment();

    /**
     * Return the current locale
     * 
     * @return locale for current run of DPU
     */
    Locale getLocale();

    /**
     * Return the current pipeline id
     * 
     * @return id of pipeline
     */
    Long getPipelineId();

    /**
     * Return the current pipeline execution id
     * 
     * @return execution ID of pipeline
     */
    Long getPipelineExecutionId();

    /**
     * Return the current DPU instance id
     * 
     * @return instance ID
     */
    Long getDpuInstanceId();
}
