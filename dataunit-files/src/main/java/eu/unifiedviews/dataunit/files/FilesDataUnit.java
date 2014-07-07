package eu.unifiedviews.dataunit.files;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.RDFData;

public interface FilesDataUnit extends DataUnit {
    public static final String SYMBOLIC_NAME_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dataunit/files/symbolicName";
    public static final String FILESYSTEM_URI_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dataunit/files/filesystemURI";

    interface Entry {
        /**
         * 
         * @return Symbolic name under which the file is stored inside this data unit.
         */
        String getSymbolicName();

        /**
         * 
         * @return URI of the real file location, for example: http://example.com/my_file.png or file://c:/Users/example/myDoc.doc
         */
        String getFilesystemURI();
    }

    interface Iteration extends AutoCloseable {
        public boolean hasNext() throws DataUnitException;

        public FilesDataUnit.Entry next() throws DataUnitException;

        @Override
        public void close() throws DataUnitException;
    }

    /**
     * 
     * @return RDF data regarding this data unit.
     */
    RDFData getRDFData();

    /**
     * List the files.
     * 
     * @return
     * @throws DataUnitException
     */
    FilesDataUnit.Iteration getFiles() throws DataUnitException;
}
