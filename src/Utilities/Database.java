package Utilities;

/**
 * singleton database class
 */
public class Database {
    private static Database single_instance = null;
    private String database;

    /**
     * private constructor
     */
    private Database()
    {
        this.database = null;
    }

    /**
     * database getter method
     * @return - database location (String)
     */
    public String getDatabase() {
        return database;
    }

    /**
     * databse setter method
     * @param database - database location (String)
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * method to provide the singleton object
     * @return - singleton database object
     */
    public static synchronized Database getDatabaseInstance()
    {
        // create the singleton object if not exists and return
        if (single_instance == null)
            single_instance = new Database();

        return single_instance;
    }
}
