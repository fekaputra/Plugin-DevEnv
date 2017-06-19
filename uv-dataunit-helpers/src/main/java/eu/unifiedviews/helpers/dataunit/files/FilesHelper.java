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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPUException;
import java.io.File;

/**
 * Helper to simplify fetching files entries from input {@link eu.unifiedviews.dataunit.files.FilesDataUnit},
 * operating with file entries, and writing file entries to output {@link eu.unifiedviews.dataunit.files.FilesDataUnit}.
 * This class should be used as the main class by DPU developers who needs helpers to operate with {@link eu.unifiedviews.dataunit.files.FilesDataUnit}.
 */
public class FilesHelper {

    /**
     * Gets file entries from the given {@link eu.unifiedviews.dataunit.files.FilesDataUnit}.
     * This method internally iterates over entries in the {@link eu.unifiedviews.dataunit.files.FilesDataUnit} and stores the entries into one {@link Map} of
     * entries.
     * Only suitable for ~10000 of entries (files) in the given {@link eu.unifiedviews.dataunit.files.FilesDataUnit}, because all entries are stored into a
     * {@link Set}. For bigger amounts of files, it is better to directly use {@link eu.unifiedviews.dataunit.files.FilesDataUnit.Iteration} to iterate over the
     * files and directly process them.
     * 
     * @param filesDataUnit
     *            data unit from which the entries are fetched
     * @return {@link Map} containing all entries in the given data unit, keys are symbolic names
     * @throws DataUnitException
     */
    public static Map<String, FilesDataUnit.Entry> getFilesMap(FilesDataUnit filesDataUnit) throws DataUnitException {
        if (filesDataUnit == null) {
            return new LinkedHashMap<>();
        }
        FilesDataUnit.Iteration iteration = filesDataUnit.getIteration();
        Map<String, FilesDataUnit.Entry> resultSet = new LinkedHashMap<>();
        try {
            while (iteration.hasNext()) {
                FilesDataUnit.Entry entry = iteration.next();
                resultSet.put(entry.getSymbolicName(), entry);
            }
        } finally {
            iteration.close();
        }
        return resultSet;
    }

    /**
     * Gets file entries from the given {@link eu.unifiedviews.dataunit.files.FilesDataUnit}.
     * This method internally iterates over entries in the {@link eu.unifiedviews.dataunit.files.FilesDataUnit} and stores the entries into one {@link Set} of
     * entries.
     * Only suitable for ~10000 of entries (files) in the given {@link eu.unifiedviews.dataunit.files.FilesDataUnit}, because all entries are stored into a
     * {@link Set}. For bigger amounts of files, it is better to directly use {@link eu.unifiedviews.dataunit.files.FilesDataUnit.Iteration} to iterate over the
     * files and directly process them.
     * 
     * @param filesDataUnit
     *            data unit from which the entries are fetched
     * @return {@link Set} containing all entries in the given data unit
     * @throws DataUnitException
     */
    public static Set<FilesDataUnit.Entry> getFiles(FilesDataUnit filesDataUnit) throws DataUnitException {
        if (filesDataUnit == null) {
            return new LinkedHashSet<>();
        }
        FilesDataUnit.Iteration iteration = filesDataUnit.getIteration();
        Set<FilesDataUnit.Entry> resultSet = new LinkedHashSet<>();
        try {
            while (iteration.hasNext()) {
                FilesDataUnit.Entry entry = iteration.next();
                resultSet.add(entry);
            }
        } finally {
            iteration.close();
        }
        return resultSet;
    }

    /**
     * Creates new empty file in the {@link eu.unifiedviews.dataunit.files.WritableFilesDataUnit} with the symbolicName metadata equal to
     * filename.
     * The physical name of the created file is generated and the file is physically stored in the working directory of the given pipeline execution.
     * It also automatically adds {@link eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelper#PREDICATE_VIRTUAL_PATH} metadata to
     * the new file, which is equal to filename.
     * Note: This function creates new connection to the RDF working store (where metadata of entries are held) anytime it is called
     * 
     * @param filesDataUnit
     *            data unit in which the file should be created
     * @param filename
     * @return Files entry pointing to the create file.
     * @throws DataUnitException
     */
    public static FilesDataUnit.Entry createFile(WritableFilesDataUnit filesDataUnit, final String filename) throws DataUnitException {
        return FilesDataUnitUtils.createFile(filesDataUnit, filename);
    }


    /**
     * Creates new empty file in the {@link eu.unifiedviews.dataunit.files.WritableFilesDataUnit} with the symbolicName metadata equal to
     * filename.
     * The physical name of the created file is generated and the file is physically stored in the working directory of the given pipeline execution.
     * It also automatically adds {@link eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelper#PREDICATE_VIRTUAL_PATH} metadata to
     * the new file, which is set to be equal to the provided virtualPath string
     * Note: This function creates new connection to the RDF working store (where metadata of entries are held) anytime it is called
     *
     * @param filesDataUnit
     *            data unit in which the file should be created
     * @param filename symbolic name of the file
     * @param virtualPath virtual path
     * @return Files entry pointing to the create file.
     * @throws DataUnitException
     */
    public static FilesDataUnit.Entry createFile(WritableFilesDataUnit filesDataUnit, final String filename, final String virtualPath) throws DataUnitException {
        return FilesDataUnitUtils.createFile(filesDataUnit, filename, virtualPath);
    }


    /**
     * Adds existing file to the {@link eu.unifiedviews.dataunit.files.WritableFilesDataUnit}.
     * It automatically creates new entry in the output data unit with the symbolicName and virtualPath metadata equal to filename.
     * The real location and the physical name of the file is as it was when it was created before calling this method. Be careful that the file
     * is not created in the working space of the given pipeline execution.
     * It also automatically adds {@link eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelper#PREDICATE_VIRTUAL_PATH} metadata to
     * the new file, which is equal to filename.
     * Note: This function creates new connection to the RDF working store (where metadata of entries are held) anytime it is called
     * 
     * @param filesDataUnit
     *            data unit to which the file should be added
     * @param file
     *            File to be added
     * @param filename
     *            The name under which the file should be added
     * @throws DPUException
     */
    public static FilesDataUnit.Entry addFile(WritableFilesDataUnit filesDataUnit, final File file, final String filename) throws DataUnitException {
        return FilesDataUnitUtils.addFile(filesDataUnit, file, filename);
    }

    /**
     * Adds existing file to the {@link eu.unifiedviews.dataunit.files.WritableFilesDataUnit}.
     * It automatically creates new entry in the output data unit with the symbolicName equal to filename and virtualPath equal to virtualPath
     * The real location and the physical name of the file is as it was when it was created before calling this method. Be careful that the file
     * is not created in the working space of the given pipeline execution.
     * It also automatically adds {@link eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelper#PREDICATE_VIRTUAL_PATH} metadata to
     * the new file, which is equal to filename.
     * Note: This function creates new connection to the RDF working store (where metadata of entries are held) anytime it is called
     *
     * @param filesDataUnit
     *            data unit to which the file should be added
     * @param file
     *            File to be added
     * @param filename
     *            The name under which the file should be added
     * @param virtualPath The virtual path of the file, which may be used e.g. by loaders at the end of the pipeline to load the file to that location
     * @throws DPUException
     */
    public static FilesDataUnit.Entry addFile(WritableFilesDataUnit filesDataUnit, final File file, final String filename, String virtualPath) throws DataUnitException {
        return FilesDataUnitUtils.addFile(filesDataUnit, file, filename, virtualPath);
    }

    /**
     * Adds existing file to the {@link eu.unifiedviews.dataunit.files.WritableFilesDataUnit}.
     * It automatically creates new entry in the output data unit with the symbolicName and virtualPath metadata equal to filename, which is automatically
     * computed as file.getName().
     * The real location and the physical name of the file is as it was when it was created before calling this method. Be careful that the file
     * is not created in the working space of the given pipeline execution.
     * It also automatically adds {@link eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelper#PREDICATE_VIRTUAL_PATH} metadata to
     * the new file, which is equal to filename.
     * Note: This function creates new connection to the RDF working store (where metadata of entries are held) anytime it is called
     * 
     * @param filesDataUnit
     *            data unit to which the file should be added
     * @param file
     *            File to be added
     * @throws DPUException
     */
    public static FilesDataUnit.Entry addFile(WritableFilesDataUnit filesDataUnit, final File file) throws DataUnitException {
        return FilesDataUnitUtils.addFile(filesDataUnit, file);

    }

    /**
     * Converts files entry to standard Java File object.
     * 
     * @param entry
     *            File entry to be converted
     * @return File representation of the given entry.
     * @throws DataUnitException
     */
    public static File asFile(FilesDataUnit.Entry entry) throws DataUnitException {
        return FilesDataUnitUtils.asFile(entry);
    }

}
