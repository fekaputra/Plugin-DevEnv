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
package eu.unifiedviews.helpers.dpu.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.helpers.dpu.context.UserContext;

import java.util.Map;

/**
 * User version of {@link ExecContext}.
 * DPU developer has access to this context from the DPU by calling {@code this.ctx}
 * 
 * @author Škoda Petr
 */
public class UserExecContext extends UserContext {

    private static final Logger log = LoggerFactory.getLogger(UserExecContext.class);

    protected final ExecContext<?> execMasterContext;

    public UserExecContext(ExecContext<?> execContext) {
        super(execContext);
        this.execMasterContext = execContext;
    }

    /**
     * This method should be used by DPU develop to get information whether the DPU was cancelled
     *
     * @return True if DPU was cancelled.
     */
    public boolean canceled() {
        return execMasterContext.getDpuContext().canceled();
    }

    /**
     * Get information whether the RDF performance optimization is enabled for the given {@link DataUnit}, i.e., may
     * change its input data graphs directly e.g. via a SPARQL Update query - this is possible when:
     * 1) the DPU is NOT executed in debug mode (because in this case we need intermediate data)
     * 2) the {@link DataUnit} of this DPU is the only {@link DataUnit} working on top of the output data produced by the preceding DPU
     * (so that input data is not accidentally changed for another parallel DPU)
     * If the DPUs may change the inputs, they may use that for further optimizations - e.g. SPARQL Update
     * DPU does not need to copy initial data but works directly on top of input data
     * It makes sense to call such method on the input {@link DataUnit}s.
     *
     * @param dataunit
     *            {@link DataUnit} which should be tested whether we can directly change the data unit
     * @return True if the {@link DataUnit} can run in optmistic mode
     */
    public boolean isPerformanceOptimizationEnabled(DataUnit dataunit) {
        try {
            return execMasterContext.getDpuContext().isPerformanceOptimizationEnabled(dataunit);
        } catch (NoSuchMethodError e) {
            log.warn("Method isPerformanceOptimizationEnabled is not available at DpuContext interface. This message may appear if you try to run DPU using 2.1.8+ API in UnifiedViews using <2.1.8 API", e);
            log.info("Performance optimization is always DISABLED");
            return false;
        }
    }

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
    public Map<String, String> getEnvironmentVariables() {
        return execMasterContext.getDpuContext().getEnvironment();
    }

    /**
     * Returns the value for the given environment variable
     * @param key The key of the environment variable
     * @return Returns the value for the given key or NULL if such execution variable is not defined.
     */
    public String getEnvironmentVariable(String key) {
        if (execMasterContext.getDpuContext().getEnvironment().containsKey(key)) {
            return execMasterContext.getDpuContext().getEnvironment().get(key);
        }
        else {
            return null;
        }
    }


    public ExecContext<?> getExecMasterContext() {
        return execMasterContext;
    }

}
