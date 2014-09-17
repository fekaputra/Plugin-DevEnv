package eu.unifiedviews.dpu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface for DPU (Data processing unit), piece of software which manipulates input data (units) into output data (units).
 *
 * @see DPUContext
 * @see DPUException
 */
public interface DPU {

    /**
     * Used to mark DPU as an Extractor. Use on main DPU class ie.
     * {@link eu.unifiedviews.dpu.DPU}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface AsExtractor {
    }

    /**
     * Used to mark DPU as a Transformer. Use on main DPU class ie.
     * {@link eu.unifiedviews.dpu.DPU}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface AsTransformer {
    }

    /**
     * Used to mark DPU as a Loader. Use on main DPU class ie.
     * {@link eu.unifiedviews.dpu.DPU}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface AsLoader {
    }

    /**
     * Main DPU execution method. DPU developer has to implement this method to make the job done.
     * Application then calls the method after it had set up any dataunit fields inside DPU class.
     *
     * If any exception is thrown then the DPU execution is considered to failed.
     * Use only checked exceptions of type (or derived) {@link DPUException} to report checked execptions.
     * Avoid throwing new {@link RuntimeException} and derived, but of course if any {@link RuntimeException} occurs
     * in third-party code, pass it by.
     *
     * @param context Execution context information
     * @throws DPUException
     * @throws InterruptedException
     */
    public void execute(DPUContext context)
            throws DPUException,
            InterruptedException;

}
