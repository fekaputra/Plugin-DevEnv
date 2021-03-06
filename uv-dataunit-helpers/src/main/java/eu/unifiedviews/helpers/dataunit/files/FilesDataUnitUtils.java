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
package eu.unifiedviews.helpers.dataunit.files;

import java.io.File;

import eu.unifiedviews.helpers.dataunit.metadata.MetadataUtils;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelper;

/**
 * Utils for working with {@link eu.unifiedviews.dataunit.files.FilesDataUnit}. DPU developer should NOT use this class directly - he should use
 * {@link FilesHelper}.
 * 
 * @author Škoda Petr
 */
public class FilesDataUnitUtils {

    /**
     * InMemory representation of File entry.
     */
    public static class InMemoryEntry implements FilesDataUnit.Entry {

        private final String fileUri;

        private final String symbolicName;

        public InMemoryEntry(String graphUri, String symbolicName) {
            this.fileUri = graphUri;
            this.symbolicName = symbolicName;
        }

        @Override
        public String getFileURIString() throws DataUnitException {
            return fileUri;
        }

        @Override
        public String getSymbolicName() throws DataUnitException {
            return symbolicName;
        }

    }

    private FilesDataUnitUtils() {

    }

    /**
     * Add file to the DataUnit. OInly file name is used as a path to file.
     * 
     * @param dataUnit
     * @param file
     * @return
     * @throws DataUnitException
     */
    public static FilesDataUnit.Entry addFile(WritableFilesDataUnit dataUnit, File file)
            throws DataUnitException {
        return addFile(dataUnit, file, file.getName());
    }

    /**
     * Add file to the DataUnit.
     * 
     * @param dataUnit
     * @param file
     *            File to add, must be under root.
     * @param symbolicName
     * @return
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    public static FilesDataUnit.Entry addFile(WritableFilesDataUnit dataUnit, File file, String symbolicName)
            throws DataUnitException {
        // Add existing file to DataUnit.
        dataUnit.addExistingFile(symbolicName, file.toURI().toString());
        // Set available metadata.
        MetadataUtils.add(dataUnit, symbolicName, FilesVocabulary.UV_VIRTUAL_PATH, symbolicName);
        // Return representing instance.
        return new InMemoryEntry(file.toURI().toString(), symbolicName);
    }

    /**
     * Add file to the DataUnit.
     *
     * @param dataUnit
     * @param file
     *            File to add, must be under root.
     * @param symbolicName
     * @return
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    public static FilesDataUnit.Entry addFile(WritableFilesDataUnit dataUnit, File file, String symbolicName, String virtualPath)
            throws DataUnitException {
        // Add existing file to DataUnit.
        dataUnit.addExistingFile(symbolicName, file.toURI().toString());
        // Set available metadata.
        MetadataUtils.add(dataUnit, symbolicName, FilesVocabulary.UV_VIRTUAL_PATH, virtualPath);
        // Return representing instance.
        return new InMemoryEntry(file.toURI().toString(), symbolicName);
    }


    /**
     * @param entry
     * @return File representation of given entry.
     * @throws DataUnitException
     */
    public static File asFile(FilesDataUnit.Entry entry) throws DataUnitException {
        return new File(java.net.URI.create(entry.getFileURIString()));
    }

    /**
     * Create file of under given path and return {@link File} to it. Also add {@link VirtualPathHelper#PREDICATE_VIRTUAL_PATH} metadata to the new file.
     * As this function create new connection is should not be used for greater number of files.
     * 
     * @param dataUnit
     * @param symbolicName
     * @return
     * @throws DataUnitException
     */
    public static FilesDataUnit.Entry createFile(WritableFilesDataUnit dataUnit, String symbolicName)
            throws DataUnitException {
        final String fileUri = dataUnit.addNewFile(symbolicName);
        MetadataUtils.add(dataUnit, symbolicName, FilesVocabulary.UV_VIRTUAL_PATH, symbolicName);
        return new InMemoryEntry(fileUri, symbolicName);
    }

    /**
     * Create file of under given path and return {@link File} to it. Also add {@link VirtualPathHelper#PREDICATE_VIRTUAL_PATH} metadata to the new file.
     * As this function create new connection is should not be used for greater number of files.
     *
     * @param dataUnit
     * @param virtualPath
     * @return
     * @throws DataUnitException
     */
    public static FilesDataUnit.Entry createFile(WritableFilesDataUnit dataUnit, String symbolicName, String virtualPath)
            throws DataUnitException {
        final String fileUri = dataUnit.addNewFile(symbolicName);
        MetadataUtils.add(dataUnit, symbolicName, FilesVocabulary.UV_VIRTUAL_PATH, virtualPath);
        return new InMemoryEntry(fileUri, symbolicName);
    }


}
