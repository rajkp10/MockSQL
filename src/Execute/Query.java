package Execute;

import Execute.Interface.IQuery;
import Utilities.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * query class with business login
 */
public class Query implements IQuery {
    /**
     * method to switch database
     * @param query - query to execute
     * @param email - email of logged in user
     * @return - status of query execution (Boolean)
     */
    @Override
    public boolean useDatabase(String query, String email) {
        try {
            // get tokens from query
            String[] tokens = query.split("\\s+");
            if (tokens.length != 2) {
                throw new Exception("insufficient query parameters.");
            }

            // open the database file
            File file = new File(DirectoryPath.getStorageDirectory() + "/" + email + "/" + tokens[1] + DirectoryPath.getFileType());

            // if file does not exist throw error
            if (!file.exists()) {
                throw new Exception("database does not exist.");
            }

            // set database instance to the file
            Database db = Database.getDatabaseInstance();
            db.setDatabase(DirectoryPath.getStorageDirectory() + "/" + email + "/" + tokens[1] + DirectoryPath.getFileType());
            return true;
        } catch (Exception e) {
            // display error
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * method to show all databases
     * @param query - query to execute
     * @param email - email of logged in user
     * @return - status of query execution (Boolean)
     */
    @Override
    public boolean showDatabases(String query, String email) {
        try {
            // open the user database directory
            File directory = new File(DirectoryPath.getStorageDirectory() + "/" + email + "/");

            // extract all database files
            File[] files = directory.listFiles((file) -> file.getName().endsWith(".txt"));

            // show all databases to user
            for (File f : files) {
                System.out.println(f.getName().split("\\.")[0]);
            }
            System.out.println("(" + files.length + ") databases found.");

            return true;
        } catch (Exception e) {
            // display error
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * method to create new database
     * @param query - query to execute
     * @param email - email of logged in user
     * @return - status of query execution (Boolean)
     */
    @Override
    public boolean createDatabase(String query, String email) {
        try {
            // extract all tokens from query
            String[] tokens = query.split("\\s+");

            // check number of tokens
            if (tokens.length != 3) {
                throw new Exception("insufficient query parameters.");
            }

            // open the current database file
            File file = new File(DirectoryPath.getStorageDirectory() + "/" + email + "/" + tokens[2] + DirectoryPath.getFileType());

            // throw error if database already exists.
            if (file.exists()) {
                throw new Exception("database already exists.");
            }

            // create new database
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("database=" + tokens[2]);
            fileWriter.write("\n");
            fileWriter.write("tables=[]");
            fileWriter.close();

            // set created database location to current database
            Database db = Database.getDatabaseInstance();
            db.setDatabase(DirectoryPath.getStorageDirectory() + "/" + email + "/" + tokens[2] + DirectoryPath.getFileType());
            return true;
        } catch (Exception e) {
            // display error
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * method to drop table
     * @param query - query to execute
     * @param email - email of logged in user
     * @return - status of query execution (Boolean)
     */
    @Override
    public boolean dropDatabase(String query, String email) {
        try {
            // extract all tokens from query
            String[] tokens = query.split("\\s+");

            // check number of tokens
            if (tokens.length != 3) {
                throw new Exception("insufficient query parameters.");
            }

            // open the database file
            File file = new File(DirectoryPath.getStorageDirectory() + "/" + email + "/" + tokens[2] + DirectoryPath.getFileType());

            // check if database already exists
            if (file.exists()) {
                // delete the database file
                file.delete();
                System.out.println("database deleted.");
                return true;
            }

            // throw error for database does not exists
            throw new Exception("database does not exist.");
        } catch (Exception e) {
            // display error
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * method to display all tables in current database
     * @param query - query to execute
     * @return - status of query execution (Boolean)
     */
    @Override
    public boolean showTables(String query) {
        try {
            // check if database context is set
            if (Database.getDatabaseInstance().getDatabase() == null) {
                // throw error if database not choosen
                throw new Exception("Database not choosen.");
            }

            // open the database file
            File file = new File(Database.getDatabaseInstance().getDatabase());

            // read the content of file
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            bufferedReader.readLine();
            String content = bufferedReader.readLine();

            // check if no table exists
            if(content.equalsIgnoreCase("tables=[]")){
                // if no tables then throw error
                throw new Exception("no tables created.");
            }

            // extract the table list
            String[] tablesList = content.substring(8, content.length() - 1).split(";");

            // display table names
            for (String table : tablesList) {
                System.out.println(table.split("\\s+")[0]);
            }
            System.out.println("(" + tablesList.length + ") tables found.");

            return true;
        } catch (Exception e) {
            // display error
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * method to create a new table
     * @param query - query to execute
     * @return - status of query execution (Boolean)
     */
    @Override
    public boolean createTable(String query) {
        try {
            // check if database context is set
            if (Database.getDatabaseInstance().getDatabase() == null) {
                throw new Exception("Database not choosen.");
            }

            // extract all the tokens in query
            String[] tokens = query.split("\\s+");

            // extract the table name from query
            String tableName = tokens[2];

            // extract all table attribute values
            String allTableAttributes = query.substring(query.indexOf("(") + 1, query.lastIndexOf(")"));
            // extract individual table attribute values
            String[] tableAttributes = allTableAttributes.split(",");

            List<Column> table = new ArrayList<>();
            for (String attribute : tableAttributes) {
                // extract table attribute name and datatype
                String[] word = attribute.trim().split("\\s+");

                // check if attribute datatype is integer or varchar
                if (!word[1].equalsIgnoreCase(Constants.INTEGER) && !word[1].substring(0, 7).equalsIgnoreCase(Constants.VARCHAR)) {
                    // if not integer or varchar then throw error
                    throw new Exception("invalid data type.");
                }

                // check if datatype contains any contraint
                if (word.length > 2) {
                    // check if constraint is primary key constraint
                    if ((word[2] + "_" + word[3]).equalsIgnoreCase(Constants.PRIMARY_KEY)) {
                        // store it in table object
                        table.add(new Column(word[0].trim(), word[1].toUpperCase().trim(), Constants.PRIMARY_KEY));
                    }
                } else {
                    // store it in table object
                    table.add(new Column(word[0].trim(), word[1].trim().toUpperCase(), null));
                }
            }

            // open the database file
            File file = new File(Database.getDatabaseInstance().getDatabase());
            StringBuilder stringBuilder = new StringBuilder();
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            stringBuilder.append(bufferedReader.readLine());

            String tables = bufferedReader.readLine();

            // extract all the tables in database
            String[] existingTables = tables.substring(8, tables.length() - 1).split(";");
            for(String tableInfo: existingTables){
                // extract the table name
                String exisitingTableName = tableInfo.split("\\s+")[0];

                // check if table name already exists
                if(exisitingTableName.equalsIgnoreCase(tableName)){
                    // if so then throw error
                    throw new Exception("table with same name already exists.");
                }
            }


            tables = tables.substring(0, tables.length() - 1);

            if (!tables.equals("tables=[")) {
                tables += ";";
            }

            // add table name to database
            tables += tableName;

            // add attribute names to database in the form of {attribute1_name:attribute2_name}
            tables += " {";
            for (Column column : table) {
                // add attribute name
                tables += column.getAttribute() + ":";
            }
            tables = tables.substring(0, tables.length() - 1) + "} {";

            // add the attribute datatype in database in the form of {attribute_dataype1(constraint):attribute_datatype2(constraint)}
            for (Column column : table) {
                // add the datatype name
                tables += column.getDataType();

                // check if any constraint
                if (column.getConstraint() != null) {
                    // if so add constraint
                    tables += "(" + column.getConstraint() + "):";
                } else {
                    tables += ":";
                }
            }
            tables = tables.substring(0, tables.length() - 1);

            // initialize the table values as empty
            tables += "} []]";

            stringBuilder.append("\n");
            stringBuilder.append(tables);
            bufferedReader.close();
            fileReader.close();

            // write the modified string to the database file
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(stringBuilder.toString());
            fileWriter.close();

            return true;
        } catch (Exception e) {
            // display error
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * method to insert data in table
     * @param query - query to execute
     * @param setAutoCommit - variable to check if transaction is on or not (Boolean)
     * @return - status of query execution (Boolean)
     */
    @Override
    public boolean insertIntoTable(String query, boolean setAutoCommit) {
        try {
            // check if database context is set
            if (Database.getDatabaseInstance().getDatabase() == null) {
                throw new Exception("Database not choosen.");
            }

            // extract the table name from query
            String queryTableName = query.split("\\s+")[2];

            // extract values to add from query
            String valuesSet = query.substring(query.indexOf("(") + 1, query.lastIndexOf(")"));

            // extract individual value
            String[] values = valuesSet.split(",");

            // open the database file
            File file = new File(Database.getDatabaseInstance().getDatabase());
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder newFileContent = new StringBuilder();

            newFileContent.append(bufferedReader.readLine()).append("\n");
            String content = bufferedReader.readLine();

            // check if database has any tables
            if (content.equals("tables=[]")) {
                // throw error if no tables available
                throw new Exception("No tables created yet.");
            }

            // extract the tables
            String tables = content.substring(8, content.length() - 1);

            // extract the table list
            String[] tableList = tables.split(";");

            String foundTableDetails = null;
            for (String table : tableList) {
                // extract individual table detail
                String tableDetails = table.split("\\[")[0].trim();
                // extract table name
                String tableName = tableDetails.split("\\s+")[0].trim();
                // extract all datatypes
                String attributeDataType = tableDetails.split("\\s+")[2].trim();
                // extract individual datatype of fields
                String[] datatypes = attributeDataType.substring(1, attributeDataType.length() - 1).split(":");
                // extract all table data
                String tableData = table.substring(table.indexOf("[") + 1, table.length() - 1);
                // extract the table data row
                String[] tableDataList = tableData.split(",");

                // if table found
                if (tableName.equalsIgnoreCase(queryTableName)) {
                    foundTableDetails = table.trim();

                    // check if number of query value parameters and table attributes are same or not
                    if (values.length != datatypes.length) {
                        // if not throw error
                        throw new Exception("invalid parameters.");
                    }

                    // check datatype of values added in the query
                    for (int i = 0; i < datatypes.length; i++) {
                        // check if table datatype is integer
                        if (datatypes[i].split("\\(")[0].equalsIgnoreCase(Constants.INTEGER)) {
                            try {
                                // check if query parameter value is also integer
                                Integer.parseInt(values[i].trim());
                            } catch (NumberFormatException e) {
                                // if not throw error
                                throw new Exception("invalid datatype.");
                            }
                        }
                        // check if table datatype is varchar
                        else if (datatypes[i].substring(0, 7).equalsIgnoreCase(Constants.VARCHAR)) {
                            // check if query parameter value is in the form of 'value'
                            if (values[i].trim().charAt(0) == '\'' && values[i].trim().charAt(values[i].trim().length() - 1) == '\'') {
                                // remove the quotes
                                values[i] = values[i].trim().substring(1, values[i].trim().length() - 1);
                            } else {
                                // if not throw error
                                throw new Exception("invalid datatype");
                            }
                        }
                    }
                    // check if table contains primary key constraint
                    for (int i = 0; i < datatypes.length; i++) {
                        // check if attribute contains primary key constraint
                        if (datatypes[i].contains(Constants.PRIMARY_KEY) && !tableData.isEmpty()) {
                            // fetch all data
                            for (String data : tableDataList) {
                                // extract data cell from data row
                                String[] dataPoint = data.substring(1, data.length() - 1).split(":");
                                // if query value already exists in table
                                if (dataPoint[i].equalsIgnoreCase(values[i])) {
                                    // throw error
                                    throw new Exception("primary key cannot have duplicate values.");
                                }
                            }
                        }
                    }
                    // break the loop if table found
                    break;
                }
            }

            // check if table found
            if (foundTableDetails == null) {
                // if not throw error
                throw new Exception("No table with name " + queryTableName + " exists.");
            }

            StringBuilder data = new StringBuilder();

            // create the data row
            data.append("{");
            for (String value : values) {
                // add the value
                data.append(value).append(":");
            }
            data.deleteCharAt(data.length() - 1).append("}");

            String newTableData = "";

            // check if table is empty
            if (foundTableDetails.endsWith("[]")) {
                // if empty add the data
                newTableData = foundTableDetails.substring(0, foundTableDetails.length() - 2) + "[" + data + "]";
                content = content.replace(foundTableDetails, newTableData);
            } else {
                // if not empty then append the data
                data.insert(0, ",");
                newTableData = foundTableDetails.substring(0, foundTableDetails.length() - 1) + data + "]";
                content = content.replace(foundTableDetails, newTableData);
            }
            newFileContent.append(content);

            // check if transaction is in progress
            if (setAutoCommit) {
                // if not then write the data in database file
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(newFileContent.toString());
                fileWriter.close();
            }

            return true;
        } catch (Exception e) {
            // check if transaction is in progress
            if(setAutoCommit) {
                // display error
                System.out.println(e.getMessage());
            }
            return false;
        }
    }

    /**
     * method to fetch data from table
     * @param query - query to execute
     * @return - status of query execution (Boolean)
     */
    @Override
    public boolean selectFromTable(String query) {
        try {
            // check if database context is set
            if (Database.getDatabaseInstance().getDatabase() == null) {
                throw new Exception("Database not choosen.");
            }

            // extract the table name from query
            String queryTableName = query.split("\\s+")[3];

            // open the database file
            File file = new File(Database.getDatabaseInstance().getDatabase());
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            bufferedReader.readLine();
            String content = bufferedReader.readLine();

            // extract the tables
            String tables = content.substring(8, content.length() - 1);

            // extract individual table
            String[] tableList = tables.split(";");

            List<Row> rows = new ArrayList<>();
            boolean foundTable = false;
            for (String table : tableList) {
                // extract all table details
                String tableDetails = table.split("\\[")[0].trim();
                // extract table name
                String tableName = tableDetails.split("\\s+")[0].trim();
                // extract attributes object
                String attributes = tableDetails.split("\\s+")[1].trim();
                // extract table data
                String tableData = table.substring(table.indexOf("[") + 1, table.length() - 1);

                // check if query table name equals existing table name
                if (tableName.equalsIgnoreCase(queryTableName)) {
                    // extract individual attributes
                    String[] attributeList = attributes.substring(1, attributes.length() - 1).split(":");
                    // extract table data rows
                    String[] tableDataList = tableData.split(",");

                    // add table data in row object
                    Row row = new Row(Arrays.asList(attributeList));
                    rows.add(row);

                    // check if table data is not empty
                    if (tableData != "") {
                        for (String data : tableDataList) {
                            // extract the data points
                            String[] cells = data.substring(1, data.length() - 1).split(":");
                            // save the data points in row cells
                            Row newRow = new Row(Arrays.asList(cells));
                            rows.add(newRow);
                        }
                    }
                    foundTable = true;
                    break;
                }
            }

            // if query table does not exist in database
            if (!foundTable) {
                // throw error
                throw new Exception("No table with name " + queryTableName + " exists.");
            }

            // display the data
            for (Row row : rows) {
                for (String cell : row.getCells()) {
                    System.out.print(cell);
                    System.out.print("\t\t");
                }
                System.out.println();
            }
            System.out.println("(" + (rows.size() - 1) + ") records fetched");

            return true;
        } catch (Exception e) {
            // display error
            System.out.println(e.getMessage());
            return false;
        }
    }
}
