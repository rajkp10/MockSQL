package Utilities;

/**
 * Column util class
 */
public class Column {
    private String attribute;
    private String dataType;
    private String constraint;

    public Column(String attribute, String dataType, String constraint) {
        this.attribute = attribute;
        this.dataType = dataType;
        this.constraint = constraint;
    }

    /**
     * attribute getter method
     * @return - attribute (String)
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * datatype getter method
     * @return - datatype (String)
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * constraint getter method
     * @return - constraint (String)
     */
    public String getConstraint() {
        return constraint;
    }
}
