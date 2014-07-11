package eu.unifiedviews.dataunit.files;

import eu.unifiedviews.dataunit.DataUnitException;

public interface WritableFilesDataUnit extends FilesDataUnit {

    interface WritableFileIteration extends FilesDataUnit.Iteration {
        public void remove() throws DataUnitException;
    }
    
    @Override
    WritableFilesDataUnit.WritableFileIteration getFileIteration() throws DataUnitException;
    
    /**
     * Get base URI (as string) where all new files should be written (only when used as output data unit).
     * On input data unit does not have any sense.
     * Input data unit is only list of files, you can not create any new files anywhere.
     * @return base path URI where all new files should be written
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    String getBaseURIString() throws DataUnitException;

    /**
     * Adds an existing file with supplied symbolic name to the data unit.
     * The symbolic name must be unique in scope of this data unit.
     * The file should be located under the getBasePath(). It is not allowed to create new files in different locations.
     * @param symbolicName symbolic name under which the file will be stored (must be unique in scope of this data unit)
     * @param existingFileURIString real file location,
     * example: http://example.com/myFile.exe, file://c:/Users/example/docs/doc.doc
     * @throws DataUnitException
     */
    void addExistingFile(String symbolicName, String existingFileURIString) throws DataUnitException;

    /**
     * Generates unique file under the getBaseURIString().
     * Returns the newly generated full file path URI to work with.
     * It does create the file on the disk, but it does not add the file into the dataunit.
     * Typical usage:
     * {@code
     * String htmlFileOutFilename = outputFileDataUnit.createFile();
     * new HTMLWriter(new File(htmlFileOutFilename)).dumpMyData(data);
     * outputFileDataUnit.addExistingFile("htmlOutput.html", htmlFileOutFilename);
     * }
     * @return URI (as string) of real location of the newly created file
     * @throws DataUnitException
     */
    String createFile() throws DataUnitException;

    /**
     * Same as {@link createFile}, but dataunit should try to create the name of newly created file to be
     * similar to proposedSymbolicName (but still unique and filesystem-safe). For better debugging
     * when browsing files on disk.
     * @param proposedSymbolicName
     * @return URI (as string) of real location of the newly created file
     * @throws DataUnitException
     */
    String createFile(String proposedSymbolicName) throws DataUnitException;
}
