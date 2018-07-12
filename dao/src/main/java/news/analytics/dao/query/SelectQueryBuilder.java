package news.analytics.dao.query;

import news.analytics.modelinfo.ModelInfo;

import java.util.LinkedList;

import static news.analytics.dao.query.QueryConstants.*;

public class SelectQueryBuilder extends AbstractQueryBuilder {

    public SelectQueryBuilder(ModelInfo modelInfo) {
        super(modelInfo);
    }

    public String getQueryString(PredicateClause predicateClause) {
        StringBuilder sb = new StringBuilder();
        sb.append(getQueryString());

        if (predicateClause != null) {
            sb.append(WHERE).append(SPACE);
            sb.append(getSQLForPredicateClause(predicateClause));
        }
        return sb.toString();
    }

    public String getQueryString() {
        LinkedList<String> columnNames = modelInfo.getColumnNames();
        StringBuilder sb = new StringBuilder();
        sb.append(SELECT).append(SPACE);
        for (int i = 0; i < columnNames.size(); i++) {
            // --> column1, column2, column3
            sb.append(columnNames.get(i));
            if (i != (columnNames.size() - 1)) {
                sb.append(COMMA);
            }
            sb.append(SPACE);
        }
        sb.append(FROM).append(SPACE);
        sb.append(modelInfo.getMappedTable());
        return sb.toString();
    }
}
