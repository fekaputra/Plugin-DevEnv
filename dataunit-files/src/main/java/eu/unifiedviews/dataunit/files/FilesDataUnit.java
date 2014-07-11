package eu.unifiedviews.dataunit.files;

import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.DataUnitException;

public interface FilesDataUnit extends MetadataDataUnit {

    static final String PREDICATE_FILE_URI = "http://linked.opendata.cz/ontology/odcs/dataunit/files/fileURI";

    interface Entry extends MetadataDataUnit.Entry {

        /**
         *
         * @return URI (as string) of the real file location, for example:
         * http://example.com/my_file.png or file://c:/Users/example/myDoc.doc
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
    FilesDataUnit.Iteration getIteration() throws DataUnitException;
}
