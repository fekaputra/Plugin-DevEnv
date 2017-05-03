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
package eu.unifiedviews.helpers.dpu.extension.faulttolerance;

import java.io.File;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
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
     * Convert {@link RDFDataUnit.Entry} to {@link IRI}
     * 
     * @param faultTolerance
     * @param entry
     * @return
     * @throws DPUException 
     */
    public static IRI asGraph(FaultTolerance faultTolerance, final RDFDataUnit.Entry entry)
            throws DPUException {
        return faultTolerance.execute(new FaultTolerance.ActionReturn<IRI>() {

            @Override
            public IRI action() throws Exception {
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
