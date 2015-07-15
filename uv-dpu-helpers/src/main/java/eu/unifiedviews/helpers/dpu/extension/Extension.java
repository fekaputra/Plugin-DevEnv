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
package eu.unifiedviews.helpers.dpu.extension;

import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.context.Context;

/**
 * Interface for objects that can be initialized.
 *
 * @author Škoda Petr
 */
public interface Extension {

    public enum ExecutionPoint {

        /**
         * It's called before DPU execution. If exception is thrown here, the DPU's user code is not executed,
         * no other add-on for this point is called. DPU execution fail. For all other points
         * {@link #execute(cz.cuni.mff.xrg.uv.boost.dpu.addon.ExecutableAddon.ExecutionPoint)} is called.
         */
        PRE_EXECUTE,
        /**
         * Is executed after used DPU code execution in innerExecute. If throws then error message is logged
         * ie. DPU's execution fail, but all other add-ons are executed.
         */
        POST_EXECUTE
    }

    /**
     * Called to set initial properties. Context is not ready to be used besides some special functions. No
     * complex code should be located in this method use {@link #afterInit()} instead.
     *
     * Configuration is not accessible during this call.
     *
     * @param param
     * @throws DPUException
     */
    public void preInit(String param) throws DPUException;

    /**
     * Called after all classes have been initialized. This method can be used to search for other services.
     *
     * @param context Same context as in
     *                {@link #preInit(cz.cuni.mff.xrg.uv.boost.dpu.context.Context, java.lang.String)}.
     */
    public void afterInit(Context context) throws DPUException;

    public interface Executable {

        /**
         *
         * @param execPoint Place where the add-on is executed.
         * @throws cz.cuni.mff.xrg.uv.boost.dpu.addon.AddonException Throw in case of failure.
         */
        void execute(ExecutionPoint execPoint) throws ExtensionException;

    }

}
