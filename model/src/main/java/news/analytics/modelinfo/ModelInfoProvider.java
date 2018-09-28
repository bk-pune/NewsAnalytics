package news.analytics.modelinfo;

import news.analytics.model.news.NewsEntity;

import java.util.HashMap;

public class ModelInfoProvider {
    private static final HashMap<Class<? extends NewsEntity>, ModelInfo> modelInfoMap = new HashMap<Class<? extends NewsEntity>, ModelInfo>();

    public static ModelInfo getModelInfo(Class newsEntityClass){
        ModelInfo modelInfo = modelInfoMap.get(newsEntityClass);
        if (modelInfo == null ){
            modelInfo = addModelInfo(newsEntityClass);
        }
        return modelInfo;
    }

    private static ModelInfo addModelInfo(Class newsEntityClass){
        ModelInfo modelInfo = modelInfoMap.get(newsEntityClass);
        if(modelInfo == null){
            modelInfo = new ModelInfo(newsEntityClass);
            modelInfoMap.put(newsEntityClass, modelInfo);
        } else {
            throw new RuntimeException("Model information for the class: "+newsEntityClass+" is already registered !");
        }
        return modelInfo;
    }
}
