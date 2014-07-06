package eu.unifiedviews.dpu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface for DPU.
 *
 * @see DPUContext
 * @see DPUException
 * @author Petyr
 */
public interface DPU {

    /**
     * Used to mark DPU as an Extractor. Use on main DPU class ie.
     * {@link eu.unifiedviews.dpu.DPU}.
     *
     * @author Petyr
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface AsExtractor {
    }

    /**
     * Used to mark DPU as a Transformer. Use on main DPU class ie.
     * {@link eu.unifiedviews.dpu.DPU}.
     *
     * @author Petyr
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface AsTransformer {
    }

    /**
     * Used to mark DPU as a Loader. Use on main DPU class ie.
     * {@link eu.unifiedviews.dpu.DPU}.
     *
     * @author Petyr
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface AsLoader {
    }

    /**
     * Execute the DPU. If any exception is thrown then the DPU execution is
     * considered to failed.
     *
     * @param context DPU's context.
     * @throws DPUException
     * @throws InterruptedException
     */
    public void execute(DPUContext context)
            throws DPUException,
            InterruptedException;

}
