package eu.unifiedviews.dataunit.files;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;
import eu.unifiedviews.dpu.DPU;

/**
 * Write enable version of {@link FilesDataUnit}, enables adding of new files or existing files into data unit.
 */
public interface WritableFilesDataUnit extends FilesDataUnit, WritableMetadataDataUnit {

    /**
     * Get base URI prefix (as string) where all new files should be created (only when used as output data unit).
     * Each data unit gets its own, unique, directory which is represented by this URI.
     * All files generated during execution of {@link DPU} which are to be added into data unit should be generated inside this location,
     * and thus their URI locations are prefixed by {@link WritableFilesDataUnit#getBaseFileURIString()}.
     * <p>
     * Example usage:
     * <p><blockquote><pre>
     * String newOutputFileURI = outputDataUnit.getBaseFileURIString() + "/" + "myData.csv";
     * File outFile = new File(java.net.URI.create(newOutputFileURI));
     * // write data to outFile
     * outputDataUnit.addExistingFile("some symbolic name", newOutputFileURI);
     * </pre></blockquote></p>
     * <p>
     * When the real fileURI is not needed to be controlled in this precise way, it is easier and more comfortable to use
     * {@link WritableFilesDataUnit#addNewFile(String)}.
     *
     * @return base path URI where all new files should be created
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    String getBaseFileURIString() throws DataUnitException;

    /**
     * Adds an existing file with supplied symbolic name to the data unit.
     * The symbolic name must be unique in scope of this data unit.
     * The real file should be located under the {@link WritableFilesDataUnit#getBaseFileURIString()} but it is not enforced (and that is useful for advanced usage).
     * If you don't know if use this function or {@link WritableFilesDataUnit#addNewFile(String)}, use the latter one.
     *
     * @param symbolicName symbolic name under which the file will be stored (must be unique in scope of this data unit)
     * @param existingFileURIString real file location, example: http://example.com/myFile.exe, file://c:/Users/example/docs/doc.doc
     * @throws DataUnitException
     */
    void addExistingFile(String symbolicName, String existingFileURIString) throws DataUnitException;

    /**
     * Generates unique file under the {@link WritableFilesDataUnit#getBaseFileURIString()}.
     * Returns the newly generated full file path URI (as string) to work with.
     * It does create the file on the disk, and it does add the file into the dataunit under provided symbolicName.
     *
     * Typical usage:
     * <p><blockquote><pre>
     * String htmlFileOutFilename = outputFileDataUnit.addNewFile("mydata");
     * new HTMLWriter(new File(htmlFileOutFilename)).dumpMyData(data);
     * </pre></blockquote></p>
     * @param symbolicName
     * @return URI (as string) of real location of the newly created file
     * @throws DataUnitException
     */
    String addNewFile(String symbolicName) throws DataUnitException;

    /**
     * Update an existing file symbolic name with new existingFileURIString.
     * The symbolic name must exists in data prior to calling this method.
     * The real file should be located under the {@link WritableFilesDataUnit#getBaseFileURIString()} but it is not enforced (and that is useful for advanced usage).
     *
     * @param symbolicName symbolic name under which the file is be stored (must be unique in scope of this data unit)
     * @param newFileURIString new real file location, example: http://example.com/myFile.exe, file://c:/Users/example/docs/doc.doc
     * @throws DataUnitException
     * @deprecated Do not use, may be removed soon and replaced by proper helper.
     */
    @Deprecated
    void updateExistingFile(String symbolicName, String newFileURIString) throws DataUnitException;
}
