package eu.unifiedviews.dataunit.files;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;

/**
 * Derived from {@link MetadataDataUnit}, {@link FilesDataUnit} stores metadata entries and each entry is enhanced by file location.
 * This data unit stores files therefore, along with any metadata attached to file entries.
 */
public interface FilesDataUnit extends MetadataDataUnit {

    /**
     * Predicate used to store metadata about file (the URI location of the file) inside storage.
     * Each metadata entry is then represented at least by triples:
     * {@code
     * <subject> p:symbolicName "name"
     * <subject> p:fileUri <file://c:/Users/uv/some/location/main.xls>
     * }
     */
    static final String PREDICATE_FILE_URI = "http://unifiedviews.eu/DataUnit/MetadataDataUnit/FilesDataUnit/fileURI";

    interface Entry extends MetadataDataUnit.Entry {

        /**
         * String stored URI representing real file location
         *
         * @return URI (as string) of the real file location, for example: "http://example.com/my_file.png" or "file://c:/Users/example/myDoc.doc"
         * @throws eu.unifiedviews.dataunit.DataUnitException
         */
        String getFileURIString() throws DataUnitException;
    }

    interface Iteration extends MetadataDataUnit.Iteration {

        @Override
        FilesDataUnit.Entry next() throws DataUnitException;
    }

    /**
     * List the files.
     *
     * @return
     * @throws DataUnitException
     */
    @Override
    FilesDataUnit.Iteration getIteration() throws DataUnitException;
}
