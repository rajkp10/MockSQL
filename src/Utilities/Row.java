package Utilities;

import java.util.List;

/**
 * Row util class
 */
public class Row {
    private List<String> cells;

    /**
     * all argument constructor
     * @param cells - list of cells (List of String)
     */
    public Row(List<String> cells) {
        this.cells = cells;
    }

    /**
     * cells getter method
     * @return - cells (List of String)
     */
    public List<String> getCells() {
        return cells;
    }
}
