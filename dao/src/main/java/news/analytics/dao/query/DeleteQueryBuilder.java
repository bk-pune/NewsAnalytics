package news.analytics.dao.query;

import news.analytics.modelinfo.ModelInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static news.analytics.dao.query.QueryConstants.*;

public class DeleteQueryBuilder<T> extends AbstractQueryBuilder {
    public DeleteQueryBuilder(ModelInfo modelInfo) {
        super(modelInfo);
    }

    public QueryAndParameters getQueryStringAndParameters(List<T> objects) {
        List<Object> valueList = getParameters(objects);
        QueryAndParameters queryAndParameters = new QueryAndParameters(getQueryString(), QueryType.DELETE, valueList);
        return queryAndParameters;
    }


    private List<Object> getParameters(List<T> objects) {
       List<Object> parameters = new ArrayList<Object>(objects.size());
        for(T entity : objects){
            Object o = modelInfo.get(entity, modelInfo.getPrimaryKeyField());
            parameters.add(o);
        }
        return parameters;
    }

    private String getQueryString(){
        StringBuilder sb = new StringBuilder();
        sb.append(DELETE).append(SPACE).append(FROM).append(SPACE);
        sb.append(modelInfo.getMappedTable()).append(SPACE);
        sb.append(WHERE).append(SPACE);
        String name = modelInfo.getPrimaryKeyColumnName();
        sb.append(name).append(SPACE).append(EQUALS).append(SPACE);
        sb.append(QUESTION_MARK);
        return sb.toString();

    }
}
