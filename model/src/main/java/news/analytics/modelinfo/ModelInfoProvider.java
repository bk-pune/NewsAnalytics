package news.analytics.modelinfo;

import news.analytics.model.NewsEntity;

import java.util.HashMap;

public class ModelInfoProvider {
    private static final HashMap<Class<? extends NewsEntity>, ModelInfo> modelInfoMap = new HashMap<Class<? extends NewsEntity>, ModelInfo>();

    public static ModelInfo getModelInfo(Class newsEntityClass){
        return modelInfoMap.get(newsEntityClass);
    }

    public static void addModelInfo(Class newsEntityClass){
        if(modelInfoMap.get(newsEntityClass) == null){
            modelInfoMap.put(newsEntityClass, new ModelInfo(newsEntityClass));
        } else {
            throw new RuntimeException("Model information for the class: "+newsEntityClass+" is already registered !");
        }
    }
}
