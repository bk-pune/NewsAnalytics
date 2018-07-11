package news.analytics.modelinfo;

import news.analytics.model.annotations.DBColumn;
import news.analytics.model.annotations.DBTable;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class ModelInfo {

    private Set<Field> fields = new HashSet<Field>();
    private Field primaryKeyField;
    private String mappedTable;

    public ModelInfo(Class newsEntityClass){
        mappedTable = getMappedTable(newsEntityClass);
        populateFieldSet(newsEntityClass);
    }

    private String getMappedTable(Class newsEntityClass) {
        DBTable table = (DBTable) newsEntityClass.getAnnotation(DBTable.class);
        return table.mappedTable();
    }

    private void populateFieldSet(Class newsEntityClass) {
        Field[] declaredFields = newsEntityClass.getDeclaredFields();
        for (Field field: declaredFields) {
            fields.add(field);
            DBColumn annotation = field.getAnnotation(DBColumn.class);
            if(annotation != null && annotation.primaryKey()){
                primaryKeyField = field;
            }

        }
    }
}
