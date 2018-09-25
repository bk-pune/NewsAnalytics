package news.analytics.dao.core;

import news.analytics.modelinfo.Converter;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class StringToSetConverter implements Converter {
    @Override
    public Object convert(String value) {
        Set<String> valueSet = (Set<String>) csvToSet(value);
        return valueSet;
    }

    private Collection<String> csvToSet(String csv) {
        Collection<String> values = new TreeSet<>();
        if(csv != null && !csv.trim().equals("")) {
            csv = csv.replaceAll("\\[", "").replaceAll("]", "");
            String[] split = csv.split(",");
            for(String tmp : split) {
                values.add(tmp.trim());
            }
        }
        return values;
    }
}
