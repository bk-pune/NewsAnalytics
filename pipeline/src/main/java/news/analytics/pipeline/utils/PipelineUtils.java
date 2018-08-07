package news.analytics.pipeline.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class PipelineUtils {

    public static String getFirstValueFromSet(Set<String> values) {
        Iterator<String> iterator = values.iterator();
        while (iterator.hasNext()){
            return iterator.next();
        }
        return null;
    }

    public static String getCommaSeparatedValues(Collection<String> values) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = values.iterator();
        while (iterator.hasNext()){
            sb.append(iterator.next()).append(",");
        }
        // remove last comma
        if(sb.length() > 0){
            return sb.substring(0, sb.length()-1);
        } else {
            return sb.toString();
        }
    }
}
