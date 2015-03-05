package eu.unifiedviews.helpers.dpu.extension.faulttolerance;

import java.io.File;
import java.util.List;

import org.openrdf.model.URI;
import eu.unifiedviews.helpers.dataunit.DataUnitUtils;
import eu.unifiedviews.helpers.dataunit.files.FilesDataUnitUtils;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.files.FilesVocabulary;
import eu.unifiedviews.helpers.dataunit.metadata.MetadataUtils;
import eu.unifiedviews.helpers.dataunit.rdf.RdfDataUnitUtils;

/**
 * Contains code for common operation with {@link FaultTolerance}.
 *
 * @author Škoda Petr
 */
public class FaultToleranceUtils {

    private FaultToleranceUtils() {

    }

    /**
     * Eager load entries into list.
     *
     * @param <T>
     * @param <E>
     * @param faultTolerance
     * @param dataUnit
     * @param resultClass
     * @return
     * @throws DPUException
     */
    public static <T extends MetadataDataUnit, E extends T.Entry> List<E> getEntries(
            FaultTolerance faultTolerance, final T dataUnit, final Class<E> resultClass) throws DPUException {

        return faultTolerance.execute(new FaultTolerance.ActionReturn<List<E>>() {

            @Override
            public List<E> action() throws Exception {
                return DataUnitUtils.getEntries(dataUnit, resultClass);
            }
        });
    }

    /**
     * Convert {@link FilesDataUnit.Entry} to {@link File}
     *
     * @param faultTolerance
     * @param entry
     * @return
     * @throws DPUException
     */
    public static File asFile(FaultTolerance faultTolerance, final FilesDataUnit.Entry entry)
            throws DPUException {
        return faultTolerance.execute(new FaultTolerance.ActionReturn<File>() {

            @Override
            public File action() throws Exception {
                return FilesDataUnitUtils.asFile(entry);
            }
        });
    }

    /**
     * Convert {@link RDFDataUnit.Entry} to {@link URI}
     * 
     * @param faultTolerance
     * @param entry
     * @return
     * @throws DPUException 
     */
    public static URI asGraph(FaultTolerance faultTolerance, final RDFDataUnit.Entry entry)
            throws DPUException {
        return faultTolerance.execute(new FaultTolerance.ActionReturn<URI>() {

            @Override
            public URI action() throws Exception {
                return RdfDataUnitUtils.asGraph(entry);
            }
        });
    }

    /**
     *
     * @param faultTolerance
     * @param dataUnit
     * @param entry
     * @return Virtual path.
     * @throws DPUException
     */
    public static String getVirtualPath(FaultTolerance faultTolerance, final MetadataDataUnit dataUnit, 
            final FilesDataUnit.Entry entry) throws DPUException {
        return faultTolerance.execute(new FaultTolerance.ActionReturn<String>() {

            @Override
            public String action() throws Exception {
                return MetadataUtils.get(dataUnit, entry, FilesVocabulary.UV_VIRTUAL_PATH);
            }
        });        
    }



}