package news.analytics.dao.core;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QueryExecutor<T> {

    public List<T> select(Connection connection, String sqlQuery, List<Object> queryParameters) throws Exception {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        if (sqlQuery.indexOf("?") != -1 && queryParameters.size() == 0)
            throw new Exception("Query needs parameters which are missing in parameters list !");

        if (queryParameters != null && queryParameters.size() > 0) {
            setParametersOnPreparedStatement(connection, preparedStatement, queryParameters);
        }

        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){

        }

        return new ArrayList<T>();
    }

    private void setParametersOnPreparedStatement(Connection connection, PreparedStatement prepareStament, List<Object> vQueryParams) throws RuntimeException {
        Object obj = null;
        try {
            for (int i = 0; i < vQueryParams.size(); i++) {

                obj = vQueryParams.get(i);

                if (obj == null)
                    prepareStament.setNull(i + 1, Types.NULL);
                else if (obj instanceof Timestamp)
                    prepareStament.setTimestamp(i + 1, (Timestamp) obj);
                else if (obj instanceof Date)
                    prepareStament.setDate(i + 1, (Date) obj);
                else if (obj instanceof java.util.Date) {
                    Date date = new Date(((java.util.Date) obj).getTime());
                    prepareStament.setDate(i + 1, date);
                } else if (obj instanceof File) {
                    FileInputStream fileInputStream = new FileInputStream((File) obj);
                    prepareStament.setBinaryStream(i + 1, fileInputStream, (int) (((File) obj).length()));
                } else if (obj instanceof Integer)
                    prepareStament.setInt(i + 1, ((Integer) obj).intValue());
                else if (obj instanceof Double)
                    prepareStament.setDouble(i + 1, ((Double) obj).doubleValue());
                else if (obj instanceof Long)
                    prepareStament.setLong(i + 1, ((Long) obj).longValue());
                else if (obj instanceof BigDecimal)
                    prepareStament.setBigDecimal(i + 1, ((BigDecimal) obj));
                else if (obj instanceof byte[])
                    prepareStament.setBytes(i + 1, (byte[]) obj);
                else if (obj instanceof Clob) {    //Oracle
                    prepareStament.setClob(i + 1, (Clob) obj);
                } else if (obj instanceof Blob) {    //Oracle
                    prepareStament.setBlob(i + 1, (Blob) obj);
                } else if (obj instanceof Boolean) {
                    prepareStament.setInt(i + 1, obj.equals(true) ? 1 : 0);
                } else
                    prepareStament.setString(i + 1, obj.toString());
            }
        } catch (Exception fnfe) {
            throw new RuntimeException(fnfe);
        }
    }
}
