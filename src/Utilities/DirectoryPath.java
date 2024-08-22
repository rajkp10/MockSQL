package Utilities;

/**
 * class for database location
 */
public class DirectoryPath {
    private final static String storageDirectory = "src/Storage/";
    private final static String fileType = ".txt";

    /**
     * storageDirectory getter method
     * @return - storage directory location (String)
     */
    public static String getStorageDirectory() {
        return storageDirectory;
    }

    /**
     * fileType getter method
     * @return - file type (String)
     */
    public static String getFileType() {
        return fileType;
    }
}
