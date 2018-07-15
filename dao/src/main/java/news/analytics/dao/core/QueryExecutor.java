package news.analytics.dao.core;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QueryExecutor<T> {

    public List<T> select(Connection connection, String sqlQuery, List<Object> queryParameters) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        if (sqlQuery.indexOf("?") != -1 && queryParameters.size() == 0)
            throw new RuntimeException("Query needs parameters which are missing in parameters list !");

        if (queryParameters != null && queryParameters.size() > 0) {
            setParametersOnPreparedStatement(connection, preparedStatement, queryParameters);
        }
        List<T> objectList = null;
        ResultSet resultSet = null;
        try {
            resultSet = preparedStatement.executeQuery();
            objectList = fromResultSetToObjects(resultSet);
        } finally {
            if(resultSet != null){
                resultSet.close();
            }
        }

        return objectList;
    }

    private List<T> fromResultSetToObjects(ResultSet resultSet) throws SQLException {
        List<T> fetched = new ArrayList<T>();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        while(resultSet.next()){
//            resultSetMetaData.getColumnName(i)
        }
        return fetched;
    }
    private void setParametersOnPreparedStatement(Connection connection, PreparedStatement preparedStatement, List<Object> vQueryParams) throws RuntimeException {
        Object obj = null;
        try {
            for (int i = 0; i < vQueryParams.size(); i++) {

                obj = vQueryParams.get(i);

                if (obj == null)
                    preparedStatement.setNull(i + 1, Types.NULL);
                else if (obj instanceof Timestamp)
                    preparedStatement.setTimestamp(i + 1, (Timestamp) obj);
                else if (obj instanceof Date)
                    preparedStatement.setDate(i + 1, (Date) obj);
                else if (obj instanceof java.util.Date) {
                    Date date = new Date(((java.util.Date) obj).getTime());
                    preparedStatement.setDate(i + 1, date);
                } else if (obj instanceof File) {
                    FileInputStream fileInputStream = new FileInputStream((File) obj);
                    preparedStatement.setBinaryStream(i + 1, fileInputStream, (int) (((File) obj).length()));
                } else if (obj instanceof Integer)
                    preparedStatement.setInt(i + 1, ((Integer) obj).intValue());
                else if (obj instanceof Double)
                    preparedStatement.setDouble(i + 1, ((Double) obj).doubleValue());
                else if (obj instanceof Long)
                    preparedStatement.setLong(i + 1, ((Long) obj).longValue());
                else if (obj instanceof BigDecimal)
                    preparedStatement.setBigDecimal(i + 1, ((BigDecimal) obj));
                else if (obj instanceof byte[])
                    preparedStatement.setBytes(i + 1, (byte[]) obj);
                else if (obj instanceof Clob) {    //Oracle
                    preparedStatement.setClob(i + 1, (Clob) obj);
                } else if (obj instanceof Blob) {    //Oracle
                    preparedStatement.setBlob(i + 1, (Blob) obj);
                } else if (obj instanceof Boolean) {
                    preparedStatement.setInt(i + 1, obj.equals(true) ? 1 : 0);
                } else
                    preparedStatement.setString(i + 1, obj.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
