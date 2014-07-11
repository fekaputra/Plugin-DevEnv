package eu.unifiedviews.dataunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Basic data unit interface. The data unit should be passed in context between
 * modules and should carry the main information. Each DataUnit has URI, this
 * can can't be changed by DPU directly. It's assigned once when DataUnit is
 * created. The URI can be obtained using {@link #getDataUnitName()}
 *
 * @author Petyr
 */
public interface DataUnit {

    /**
     * Used to announced that given
     * {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit} should be used as
     * input. Can not be use together with {@link AsOutput}. If DPU contains
     * more than one output the name should be provided for all the output data
     * units. Use only letters [a-z, A-Z], numbers [0-9] and '_' in
     * {@link #name()}.Usage of other chars is not allowed.
     *
     * @author Petyr
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface AsInput {

        /**
         * Name which identify input. Name is obligatory.
         * {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}.
         *
         * @return Name of {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}.
         */
        public String name();

        /**
         * @return False the execution failed if there is no suitable
         * {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit} that can be used.
         */
        public boolean optional() default false;

        /**
         * Return description that will be presented to the user.
         *
         * @return {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}'s
         * description
         */
        public String description() default "";

    }

    /**
     * Used to announced that given
     * {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit} should be used as
     * output. Can not be use together with {@link AsInput}. If DPU contains
     * more than one output the name should be provided for all the output data
     * units. Use only letters [a-z, A-Z], numbers [0-9] and '_' in
     * {@link #name()}.Usage of other chars is not allowed.
     *
     * @author Petyr
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface AsOutput {

        /**
         * @return Name of output
         * {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}.
         */
        public String name();

        /**
         * @return {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}'s
         * description will be visible to the user.
         */
        public String description() default "";

        /**
         * @return If true that output can be null if not used by other dpu.
         */
        public boolean optional() default false;

    }
}
