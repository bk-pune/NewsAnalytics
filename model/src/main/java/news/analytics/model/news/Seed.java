package news.analytics.model.news;

import news.analytics.model.annotations.ConstraintType;
import news.analytics.model.annotations.DBColumn;
import news.analytics.model.annotations.DBConstraint;
import news.analytics.model.annotations.DBTable;
import news.analytics.model.constants.DataType;

/**
 * Represents a URI which is to be crawled by the crawler.
 */
@DBTable(mappedTable = "SEEDS")
public class Seed extends NewsEntity {
    private static final long serialVersionUID = 1212178891279811324L;

    @DBColumn(column = "ID", dataType = DataType.LONG, primaryKey = true, constraints = @DBConstraint(constraintType = ConstraintType.PRIMARY_KEY, constraintName = "ID_PK_SEEDS"))
    private Long id;

    @DBColumn(column = "URI", dataType = DataType.VARCHAR, constraints = @DBConstraint(constraintType = ConstraintType.UNIQUE, constraintName = "URI_UNIQUE_SEEDS"))
    private String uri;

    @DBColumn(column = "FETCH_STATUS", dataType = DataType.VARCHAR, nullable = false)
    private String fetchStatus;

    @DBColumn(column = "HTTP_CODE", dataType = DataType.SHORT, nullable = false)
    private Short httpCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFetchStatus() {
        return fetchStatus;
    }

    public void setFetchStatus(String fetchStatus) {
        this.fetchStatus = fetchStatus;
    }

    public Short getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(Short httpCode) {
        this.httpCode = httpCode;
    }
}
