package news.analytics.model.constants;

public enum DataType {

    BLOB("BLOB"),
    CLOB("CLOB"),
    VARCHAR("VARCHAR"),
    LONG("LONG"),
    INTEGER("INTEGER"),
    FLOAT("FLOAT"),
    DOUBLE("DOUBLE"),
    BOOLEAN("BOOLEAN"),
    DATE("DATE"),
    NONE("NONE"),
    TIMESTAMP("TIMESTAMP");

    String dataType;

    DataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDatatype(){
        return dataType;
    }
}
