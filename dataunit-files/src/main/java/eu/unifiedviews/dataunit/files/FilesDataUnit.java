package eu.unifiedviews.dataunit.files;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;

public interface FilesDataUnit extends DataUnit {

    public static final String PREDICATE_SYMBOLIC_NAME = "http://linked.opendata.cz/ontology/odcs/dataunit/files/symbolicName";
    public static final String PREDICATE_FILE_URI = "http://linked.opendata.cz/ontology/odcs/dataunit/files/fileURI";

    interface FileEntry {

        /**
         *
         * @return Symbolic name under which the file is stored inside this data
         * unit.
         * @throws eu.unifiedviews.dataunit.DataUnitException
         */
        String getSymbolicName() throws DataUnitException;

        /**
         *
         * @return URI (as string) of the real file location, for example:
         * http://example.com/my_file.png or file://c:/Users/example/myDoc.doc
         * @throws eu.unifiedviews.dataunit.DataUnitException
         */
        String getFileURIString() throws DataUnitException;
    }

    interface FileIteration extends AutoCloseable {

        public boolean hasNext() throws DataUnitException;

        public FilesDataUnit.FileEntry next() throws DataUnitException;

        @Override
        public void close() throws DataUnitException;
    }

    /**
     * List the files.
     *
     * @return
     * @throws DataUnitException
     */
    FilesDataUnit.FileIteration getFileIteration() throws DataUnitException;
}
