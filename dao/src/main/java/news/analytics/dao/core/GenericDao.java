package news.analytics.dao.core;

import news.analytics.dao.query.*;
import news.analytics.model.NewsEntity;
import news.analytics.model.constants.DataType;
import news.analytics.modelinfo.ModelInfo;
import news.analytics.modelinfo.ModelInfoProvider;

import java.io.*;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericDao<T extends NewsEntity> extends QueryExecutor<T> {
    private ModelInfo modelInfo;
    private SelectQueryBuilder selectQueryBuilder;
    private UpdateQueryBuilder updateQueryBuilder;
    private DeleteQueryBuilder deleteQueryBuilder;
    private InsertQueryBuilder insertQueryBuilder;

    public GenericDao(Class<T> clazz) {
        modelInfo = ModelInfoProvider.getModelInfo(clazz);
        selectQueryBuilder = new SelectQueryBuilder(modelInfo);
        updateQueryBuilder = new UpdateQueryBuilder(modelInfo);
        deleteQueryBuilder = new DeleteQueryBuilder(modelInfo);
        insertQueryBuilder = new InsertQueryBuilder(modelInfo);
    }

    public List<T> insert(Connection connection, List<T> objects) throws SQLException {
        try {
            QueryAndParameters queryStringAndParameters = insertQueryBuilder.getQueryStringAndParameters(objects);
            executeInsert(connection, queryStringAndParameters.getQueryString(), (List<List<Object>>) queryStringAndParameters.getParameters());
            connection.commit();
        } finally {
            connection.close();
        }
        return objects;
    }

    public List<T> update(Connection connection, List<T> objects) {
        return new ArrayList<T>();
    }

    public List<T> delete(Connection connection, List<T> objects) throws SQLException {
        try {
            QueryAndParameters queryStringAndParameters = deleteQueryBuilder.getQueryStringAndParameters(objects);
            String sqlQuery = "";
            sqlQuery = queryStringAndParameters.getQueryString();
            List<Object> parameters = (List<Object>) queryStringAndParameters.getParameters();
            executeDelete(connection, sqlQuery, parameters);
            connection.commit();
            return objects;
        } finally {
            connection.close();
        }
    }

    public List<T> select(Connection connection, PredicateClause predicateClause) throws SQLException, IllegalAccessException, IOException, InstantiationException {
        try {
            QueryAndParameters queryStringAndParameters = selectQueryBuilder.getQueryStringAndParameters(predicateClause);
            String sqlQuery = "";
            List<Object> parameters = null;
            sqlQuery = queryStringAndParameters.getQueryString();
            parameters = (List<Object>) queryStringAndParameters.getParameters();
            ResultSet resultSet = executeSelect(connection, sqlQuery, parameters);
            List<T> objects = fromResultSetToObjects(resultSet);
            return objects;
        } finally {
            connection.close();
        }
    }

    private List<T> fromResultSetToObjects(ResultSet resultSet) throws SQLException, IOException, IllegalAccessException, InstantiationException {
        List<T> fetched = new ArrayList<T>();
        Map<String, Field> fieldMap = modelInfo.getFieldMap();
        try {
            while (resultSet.next()) {
                T instance = (T) modelInfo.getNewsEntityClass().newInstance();
                int columnIndex = 1;
                for (Map.Entry<String, Field> fieldMapEntry : fieldMap.entrySet()) {
                    Field field = fieldMapEntry.getValue();
                    DataType sqlDatatypeForField = modelInfo.getSQLDatatypeForField(field.getName());
                    Object value = getValue(resultSet, columnIndex, sqlDatatypeForField);
                    modelInfo.setValueToObject(instance, value, field);
                    columnIndex++;
                }
                fetched.add(instance);
            }
        } finally {
            resultSet.close();
        }
        return fetched;
    }


    /**
     * Returns the value from result set as an object.
     *
     * @param resultSet     SQL ResultSet
     * @param index         column index of the result set
     * @param fieldDatatype Type of the field in model
     * @return
     * @throws IOException
     * @throws SQLException
     */
    private Object getValue(ResultSet resultSet, int index, DataType fieldDatatype) throws SQLException {
        Object value = null;
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        String columnName = resultSetMetaData.getColumnName(index);
        try {
            switch (fieldDatatype) {
                case BLOB:
                    value = getObjectFromBlob(resultSet.getBlob(index));
                    break;
                case CLOB:
                    value = getObjectFromClob(resultSet.getClob(index));
                    break;
                case VARCHAR:
                    value = resultSet.getString(index);
                    break;
                case LONG:
                    value = resultSet.getLong(index);
                    break;
                case INTEGER:
                    value = resultSet.getInt(index);
                    break;
                case BOOLEAN:
                    value = resultSet.getBoolean(index);
                    break;
                case FLOAT:
                    value = resultSet.getFloat(index);
                    break;
                case DOUBLE:
                    value = resultSet.getDouble(index);
                    break;
                case DATE:
                    value = resultSet.getDate(index);
                    break;
                case TIMESTAMP:
                    value = resultSet.getTimestamp(index);
                    break;
            }
        } catch (SQLException e) {
            throw e;
        }
        return value;
    }

    private String getObjectFromClob(Clob clob) throws SQLException {
        StringBuilder sb = new StringBuilder();
        try {
            Reader reader = clob.getCharacterStream();
            BufferedReader br = new BufferedReader(reader);

            String line;
            while (null != (line = br.readLine())) {
                sb.append(line);
            }
            br.close();
        } catch (SQLException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    private Object getObjectFromBlob(Blob blob) throws SQLException {
        Object object = null;
        try {
            if (blob != null) {
                InputStream inputStream = blob.getBinaryStream();
                ObjectInput objectInput = new ObjectInputStream(inputStream);
                try {
                    object = objectInput.readObject();
                } finally {
                    if (objectInput != null)
                        objectInput.close();
                    if (inputStream != null)
                        inputStream.close();
                }
            }
        } catch (SQLException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}
