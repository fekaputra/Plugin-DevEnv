package eu.unifiedviews.dataunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Basic data unit interface. The data unit should be passed in context between
 * modules and should carry the main information.
 *
 * @author Petyr
 */
public interface DataUnit {

    /**
     * Used to mark that given
     * {@link DataUnit} will be handled as
     * input. Can not be used together with {@link AsOutput}. The name must be provided for all the input data
     * units. Use only letters [a-z, A-Z], numbers [0-9] and '_' in
     * {@link #name()}.Usage of other chars is not allowed.
     *
     * @author Petyr
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface AsInput {

        /**
         * Name which identify input. Name is obligatory.
         * {@link DataUnit}.
         *
         * @return Name of {@link DataUnit}.
         */
        String name();

        /**
         * @return False the execution failed if there is no suitable
         * {@link DataUnit} that can be used.
         */
        boolean optional() default false;

        /**
         * Return description that will be presented to the user.
         *
         * @return {@link DataUnit}'s
         * description
         */
        String description() default "";

    }

    /**
     * Used to mark that given
     * {@link DataUnit} will be handled as
     * output. Can not be use together with {@link AsInput}. The name must be provided for all the output data
     * units. Use only letters [a-z, A-Z], numbers [0-9] and '_' in
     * {@link #name()}.Usage of other chars is not allowed.
     *
     * @author Petyr
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface AsOutput {

        /**
         * @return Name of output
         * {@link DataUnit}.
         */
        String name();

        /**
         * @return {@link DataUnit}'s
         * description will be visible to the user.
         */
        String description() default "";

        /**
         * @return If true that output can be null if not used by other DPU.
         */
        boolean optional() default false;

    }
}
