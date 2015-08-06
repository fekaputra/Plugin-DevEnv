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
package eu.unifiedviews.dataunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import eu.unifiedviews.dpu.DPU;

/**
 * The data unit encapsulates data passed between plugins. See {@link DPU} for example usage.
 *
 * Each data unit is either of type input or output.
 * Input data unit is filled with data passed from preceding {@link DPU}
 */
public interface DataUnit {

    /**
     * Used to mark that given {@link DataUnit} will be handled as input of {@link DPU}. Cannot be used together with {@link AsOutput}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface AsInput {

        /**
         * Description that will be presented to the user.
         *
         * @return {@link DataUnit}'s description
         */
        String description() default "";

        /**
         * Obligatory name which identifies the dataunit. Allowed pattern: [a-zA-Z][a-zA-Z0-9_]*
         *
         * @return Name of {@link DataUnit}.
         */
        String name();

        /**
         * If set to true, it is optional to connect data flow into this dataunit. Otherwise, data flow has to be connected.
         *
         * @return False the execution failed if there is no suitable {@link DataUnit} that can be used.
         */
        boolean optional() default false;

    }

    /**
     * Used to mark that given {@link DataUnit} will be handled as output of {@link DPU}. Cannot be used together with {@link AsInput}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface AsOutput {

        /**
         * Description that will be presented to the user.
         *
         * @return {@link DataUnit}'s
         *         description will be visible to the user.
         */
        String description() default "";

        /**
         * Obligatory name which identifies the dataunit. Allowed pattern: [a-zA-Z][a-zA-Z0-9_]*
         *
         * @return Name of output {@link DataUnit}.
         */
        String name();

        /**
         * If set to true, it is optional to connect data flow into this dataunit. Otherwise, data flow has to be connected.
         *
         * When user chooses to not connect optional dataunit, the annotated field can be set to null in {@link DPU} by application
         * to inform {@link DPU} execution that this output should not be generated at all (as it is unused in further execution).
         *
         * @return true output field can be null if not connected to another {@link DPU}
         */
        boolean optional() default false;
    }
}
