package Parser;

import java.util.StringTokenizer;

import Execute.Interface.IQuery;
import Utilities.Constants;
import Execute.Query;
import Utilities.Transaction;

/**
 * QueryParser class to parse the query
 */
public class QueryParser {
    private Transaction transaction = null;
    private boolean setAutoCommit = true;

    /**
     * method to parse the query
     * @param query
     * @param email
     */
    public void parseQuery(String query, String email) {
        // status variable to check
        boolean status = false;
        String queryType = "";

        // check for query syntax of transaction
        if (query.toUpperCase().contains(Constants.BEGIN + " " + Constants.TRANSACTION)) {
            this.setAutoCommit = false;
        }

        if(query.toUpperCase().contains(Constants.END + " " + Constants.TRANSACTION)){
            this.setAutoCommit = true;
        }

        // check if query is of type commit or rollback
        if (query.substring(0, query.length() - 1).equalsIgnoreCase(Constants.COMMIT) || query.substring(0, query.length() - 1).equalsIgnoreCase(Constants.ROLLBACK)) {
            // set auto commit to true
            query = query.substring(0 ,query.length() - 1);
        }

        // check query syntax if not a transaction
        if (this.setAutoCommit) {
            if (query.endsWith(";")) {
                query = query.substring(0, query.length() - 1);
            } else {
                System.out.println("invalid query.");
                return;
            }
        }

        // convert the query string into tokens
        StringTokenizer stringTokenizer = new StringTokenizer(query);

        // check if query has more tokens
        if (stringTokenizer.hasMoreTokens()) {
            // select next string token
            queryType = stringTokenizer.nextToken();
        } else {
            return;
        }

        // query type check
        IQuery executeQuery = new Query();
        switch (queryType.toUpperCase()) {
            // if first token is USE
            case Constants.USE:
                if (stringTokenizer.hasMoreTokens()) {
                    // call the useDatabase method
                    status = executeQuery.useDatabase(query, email);
                }
                break;
            // if first token is SHOW
            case Constants.SHOW:
                // check if query has more string tokens
                if (stringTokenizer.hasMoreTokens()) {
                    String nextToken = stringTokenizer.nextToken();
                    // if next token is DATABASES
                    if (nextToken.equalsIgnoreCase(Constants.DATABASES)) {
                        // call the showDatabase method
                        status = executeQuery.showDatabases(query, email);
                    }
                    // if next token is TABLES
                    else if (nextToken.equalsIgnoreCase(Constants.TABLES)) {
                        // call the showTables method
                        status = executeQuery.showTables(query);
                    }
                }
                break;
            // if first token is CREATE
            case Constants.CREATE:
                if (stringTokenizer.hasMoreTokens()) {
                    String nextToken = stringTokenizer.nextToken();
                    // if query has more tokens and next token is DATABASE
                    if (nextToken.equalsIgnoreCase(Constants.DATABASE)) {
                        // call the createDatabase method
                        status = executeQuery.createDatabase(query, email);
                    }
                    // if query has more tokens and next token is TABLE
                    else if (nextToken.equalsIgnoreCase(Constants.TABLE)) {
                        // call the createTable method
                        status = executeQuery.createTable(query);
                    }
                }
                break;
            // if first token is DROP
            case Constants.DROP:
                if (stringTokenizer.hasMoreTokens() && stringTokenizer.nextToken().equalsIgnoreCase(Constants.DATABASE)) {
                    status = executeQuery.dropDatabase(query, email);
                }
                break;
            // if first token is INSERT
            case Constants.INSERT:
                // if next token is INTO
                if (stringTokenizer.hasMoreTokens() && stringTokenizer.nextToken().equalsIgnoreCase(Constants.INTO)) {
                    // if query has more tokens
                    if (stringTokenizer.hasMoreTokens()) {
                        // skip the next token for table name
                        stringTokenizer.nextToken();
                        // if query string has more tokens and next token is VALUES
                        if (stringTokenizer.hasMoreTokens() && stringTokenizer.nextToken().equalsIgnoreCase(Constants.VALUES)) {
                            // check if transaction is on
                            if (!setAutoCommit) {
                                transaction.addToTempTransaction(query);
                            }
                            // call the insertIntoTable method
                            status = executeQuery.insertIntoTable(query, setAutoCommit);
                            // if status is false and transaction is on
//                            if (!status && !setAutoCommit) {
//                                // set transaction as failed
//                                transaction.setStatus(status);
//                            }
                        }
                    }
                }
                break;
            // if first token is SELECT
            case Constants.SELECT:
                // if query string has more tokens and next token is *
                if (stringTokenizer.hasMoreTokens() && stringTokenizer.nextToken().equals("*")) {
                    // if query string has more tokens and next token is FROM
                    if (stringTokenizer.hasMoreTokens() && stringTokenizer.nextToken().equalsIgnoreCase(Constants.FROM)) {
                        // call the selectFromTable method
                        status = executeQuery.selectFromTable(query);
                    }
                }
                break;
            // if first token is BEGIN
            case Constants.BEGIN:
                // if query string has more tokens and next token is TRANSACTION
                if (stringTokenizer.hasMoreTokens() && stringTokenizer.nextToken().equalsIgnoreCase(Constants.TRANSACTION)) {
                    // if query string does not have any more tokens
                    if (!stringTokenizer.hasMoreTokens()) {
                        // set auto commit to false
                        this.setAutoCommit = false;
                        // create a new transaction object to start the transaction
                        this.transaction = new Transaction();
                        status = true;
                    }
                }
                else{
                    this.setAutoCommit = true;
                }
                break;
            case Constants.END:
                // if query string has more tokens and next token is TRANSACTION
                if (stringTokenizer.hasMoreTokens() && stringTokenizer.nextToken().equalsIgnoreCase(Constants.TRANSACTION)) {
                    // if query string does not have any more tokens
                    if (!stringTokenizer.hasMoreTokens()) {
                        // set auto commit to false
                        this.setAutoCommit = true;
                        // execute all the queries in transaction
                        this.transaction.executeTransaction(new QueryParser());
                        // remove the queries from the transaction object
                        this.transaction.emptyTempTransaction();
                        // set transaction object to null
                        status = true;
                    }
                }
                else{
                    this.setAutoCommit = true;
                }
                break;
            // if first token is COMMIT
            case Constants.COMMIT:
                // check if all queries in transaction are valid queries
                transaction.transferToFinalCommands();
                break;
            // if first token is ROLLBACK
            case Constants.ROLLBACK:
                // empty the temp transactio list
                transaction.emptyTempTransaction();
                break;
            default:
                break;
        }

        // check if transaction is on
        if (setAutoCommit) {
            // check the status flag
            if (status) {
                System.out.println("query executed.");
            } else {
                System.out.println("invalid query.");
            }
        }
    }
}
