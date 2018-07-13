package news.analytics.dao.core;

import news.analytics.dao.query.DeleteQueryBuilder;
import news.analytics.dao.query.InsertQueryBuilder;
import news.analytics.dao.query.SelectQueryBuilder;
import news.analytics.dao.query.UpdateQueryBuilder;
import news.analytics.model.NewsEntity;
import news.analytics.modelinfo.ModelInfo;
import news.analytics.modelinfo.ModelInfoProvider;

import java.util.ArrayList;
import java.util.List;

public class GenericDao<T extends NewsEntity> {
    private ModelInfo modelInfo;
    private SelectQueryBuilder selectQueryBuilder;
    private UpdateQueryBuilder updateQueryBuilder;
    private DeleteQueryBuilder deleteQueryBuilder;
    private InsertQueryBuilder insertQueryBuilder;

    public GenericDao(Class clazz) {
        modelInfo = ModelInfoProvider.getModelInfo(clazz);
        selectQueryBuilder = new SelectQueryBuilder(modelInfo);
        updateQueryBuilder = new UpdateQueryBuilder(modelInfo);
        deleteQueryBuilder = new DeleteQueryBuilder(modelInfo);
        insertQueryBuilder = new InsertQueryBuilder(modelInfo);
    }

    public List<T> insert(List<T> entityList){
        return null;
    }

    public List<T> update(List<T> entityList){
        return new ArrayList<T>();
    }

    public List<T> delete(List<T> entityList){
        return new ArrayList<T>();
    }

    public List<T> select(){
        return new ArrayList<T>();
    }
}
