/*******************************************************************************
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
 *******************************************************************************/
package eu.unifiedviews.helpers.dpu.exec;

import eu.unifiedviews.helpers.dpu.context.Context;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;

/**
 *
 * @author Škoda Petr
 */
public class ExecContext<CONFIG> extends Context<CONFIG> {

    /**
     * Owner DPU instance.
     */
    protected final AbstractDpu<CONFIG> dpu;

    /**
     * Execution context.
     */
    protected DPUContext dpuContext = null;

    /**
     * DPU's configuration.
     */
    protected CONFIG dpuConfig = null;

    /**
     * Cause given DPU initialization. Must not be called in constructor!
     *
     * @param dpuClass
     * @param dpuInstance
     * @param ontology
     * @throws DPUException
     */
    public ExecContext(AbstractDpu<CONFIG> dpuInstance) {
        super((Class<AbstractDpu<CONFIG>>) dpuInstance.getClass(), dpuInstance);
        this.dpu = dpuInstance;
    }

    public CONFIG getDpuConfig() {
        return dpuConfig;
    }

    public void setDpuConfig(CONFIG config) {
        this.dpuConfig = config;
    }

    public DPUContext getDpuContext() {
        return dpuContext;
    }

    public AbstractDpu<CONFIG> getDpu() {
        return dpu;
    }

    /**
     * Initialize context before first use.
     * 
     * @param configAsString
     * @param dpuContext
     * @throws DPUException
     */
    protected void init(String configAsString, DPUContext dpuContext) throws DPUException {
        this.dpuContext = dpuContext;
        init(configAsString, dpuContext.getLocale(), this.dpu.getClass().getClassLoader());
    }

}
