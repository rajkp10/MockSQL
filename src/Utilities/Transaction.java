package Utilities;

import Parser.QueryParser;

import java.util.ArrayList;
import java.util.List;

/**
 * transaction class
 */
public class Transaction {
    private List<String> tempCommands;
    private List<String> finalCommands;

    /**
     * no argument constructor
     */
    public Transaction() {
        this.tempCommands = new ArrayList<>();
        this.finalCommands = new ArrayList<>();
    }

    /**
     * method to add query in transaction
     * @param query - String
     */
    public void addToTempTransaction(String query) {
        this.tempCommands.add(query);
    }

    /**
     * method to empty the transaction list
     */
    public void emptyTempTransaction() {
        this.tempCommands.clear();
    }

    /**
     * method to transfer the temp list queries to final list
     */
    public void transferToFinalCommands() {
        finalCommands.addAll(tempCommands);
    }

    /**
     * method to execute statements of transaction
     * @param queryParser - queryParser object
     */
    public void executeTransaction(QueryParser queryParser) {
        // execute queries only if all queries are valid
        for (String query : finalCommands) {
            queryParser.parseQuery(query, null);
        }
    }
}
