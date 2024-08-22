package Execute.Interface;

/**
 * interface for query
 */
public interface IQuery {
    public boolean useDatabase(String query, String email);
    public boolean showDatabases(String query, String email);
    public boolean createDatabase(String query, String email);
    public boolean dropDatabase(String query, String email);
    public boolean showTables(String query);
    public boolean createTable(String query);
    public boolean insertIntoTable(String query, boolean setAutoCommit);
    public boolean selectFromTable(String query);
}
