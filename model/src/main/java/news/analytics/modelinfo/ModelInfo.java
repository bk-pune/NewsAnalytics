package news.analytics.modelinfo;

import news.analytics.model.NewsEntity;
import news.analytics.model.annotations.DBColumn;
import news.analytics.model.annotations.DBTable;
import news.analytics.model.constants.DataType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ModelInfo<T> {

    private Set<Field> fields = new HashSet<Field>();
    private LinkedList<String> columnNames = new LinkedList<String>();
    private Map<String, Field> fieldMap = new LinkedHashMap<String, Field>();
    private Map<String, DBColumn> fieldDBTableAnnotationMap = new HashMap<String, DBColumn>();
    private Field primaryKeyField;
    private String mappedTable;
    private Class newsEntityClass;

    public ModelInfo(Class newsEntityClass){
        this.newsEntityClass = newsEntityClass;
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
            if (annotation != null) {
                if (annotation.primaryKey()) {
                    primaryKeyField = field;
                }
                fieldMap.put(field.getName(), field);
                columnNames.add(annotation.column());
                fieldDBTableAnnotationMap.put(field.getName(), annotation);
            }
        }
    }

    public LinkedList<String> getColumnNames() {
        return columnNames;
    }

    public Field getPrimaryKeyField() {
        return primaryKeyField;
    }

    public String getPrimaryKeyColumnName(){
        DBColumn dbColumn = primaryKeyField.getAnnotation(DBColumn.class);
        return dbColumn.column();
    }

    public String getMappedTable() {
        return mappedTable;
    }

    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }

    public DataType getSQLDatatypeForField(String fieldName) {
        DBColumn dbColumn = fieldDBTableAnnotationMap.get(fieldName);
        return dbColumn.dataType();
    }

    public Class getNewsEntityClass() {
        return newsEntityClass;
    }

    public <T extends NewsEntity> void setValueToObject(T instance, Object value, Field field) {
        try {
            Method method;
            method = newsEntityClass.getMethod("set" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1), field.getType());
            method.invoke(instance, value);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Object get(T o, Field field) {
        String column = field.getName();
        Method method;
        try{
            if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
                method = newsEntityClass.getMethod("is" + Character.toUpperCase(column.charAt(0)) + column.substring(1));
            } else {
                method = newsEntityClass.getMethod("get" + Character.toUpperCase(column.charAt(0)) + column.substring(1));
            }
            return method.invoke(o);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
