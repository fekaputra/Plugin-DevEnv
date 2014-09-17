package eu.unifiedviews.helpers.dataunit.fileshelper;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;

/**
 * Helper to make various tasks with {@link FilesDataUnit} friendly.
 */
public class FilesHelper {
    /**
     * Exhaust {@link eu.unifiedviews.dataunit.files.FilesDataUnit.Iteration} (obtained using {@link eu.unifiedviews.dataunit.files.FilesDataUnit#getIteration()}) into one {@link Set} of entries.
     * Beware - if the {@link eu.unifiedviews.dataunit.files.FilesDataUnit} contains milions or more entries, storing all of this in single {@link Set} is not a good idea.
     * Only suitable for work with ~100000 of entries (files)
     *
     * @param filesDataUnit data unit from which the iteration will be obtained and exhausted
     * @return {@link Set} containing all entries
     * @throws DataUnitException
     */
    public static Set<FilesDataUnit.Entry> getFiles(FilesDataUnit filesDataUnit) throws DataUnitException {
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
}
