package news.analytics.pipeline.config;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides meta-data configuration for given news agency.
 */
public class NewsMetaConfigProvider {
    private static Map<String, String> rawConfigCache = new HashMap<String, String>();

    private static ArrayList<String> jsonKeys = Lists.newArrayList("valueLocatorType", "tagIdentifierTagName", "tagIdentifierAttributeName",
            "tagIdentifierAttributeValue", "valueAttributeName");

    private static String loadRawConfig(String newsAgency) throws IOException {
        InputStream inputStream = NewsMetaConfigProvider.class.getClassLoader().getResourceAsStream(newsAgency+".config");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String tmp = "";
        StringBuilder sb = new StringBuilder();
        while ((tmp = br.readLine()) != null){
            sb.append(tmp);
        }
        br.close();
        return sb.toString();
    }

    public static ArrayList<String> getJsonKeys() {
        return jsonKeys;
    }

    public static String getRawConfig(String newsAgency) throws IOException {
        String rawConfig = rawConfigCache.get(newsAgency);
        if(rawConfig == null){
            rawConfig = loadRawConfig(newsAgency);
            rawConfigCache.put(newsAgency, rawConfig);
        }
        return rawConfig;
    }
}