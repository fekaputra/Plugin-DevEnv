package eu.unifiedviews.helpers.dataunit.fileshelper;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;

public class FilesHelper {
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
