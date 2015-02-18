package eu.unifiedviews.helpers.test.files;

/**
 * Factory for test files data units.
 * 
 * @author Škoda Petr
 */
public class FilesDataUnitFactory {

    private FilesDataUnitFactory() {
        
    }

    public static FilesDataUnitRead createRead() {
        return new FilesDataUnitRead();
    }

}
