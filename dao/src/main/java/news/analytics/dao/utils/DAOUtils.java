package news.analytics.dao.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DAOUtils {
    private static final JsonFactory jsonFactory = new JsonFactory();

    /**
     * @param jsonString
     * @return
     * @throws Exception
     */
    public Object fromJson(String jsonString, Class entityClass) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Object ret = null;
        JsonParser jsonParser = jsonFactory.createParser(jsonString);
        try {
            JsonToken jsonToken = jsonParser.nextToken();
            if (jsonToken == JsonToken.START_ARRAY) {
                ret = jsonToJAVACollection(jsonString, ArrayList.class, entityClass);

            } else if (jsonToken == JsonToken.START_OBJECT) {
                ret = mapper.readValue(jsonString, entityClass);
            }
            return ret;
        } finally {
            jsonParser.close();
        }
    }

    /**
     * ArrayList<NewsEntity> ->
     * @param jsonString
     * @param collectionClass
     * @param valueType
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T jsonToJAVACollection(String jsonString, Class<T> collectionClass, Class valueType) throws Exception {
        Object o = null;
        if (jsonString != null) {
            ObjectMapper or = new ObjectMapper();
            Type[] types = new Type[1];
            types[0] = valueType;
            Type collectionType = getParameterizedType(collectionClass, types, null);
            JavaType javaType = or.getTypeFactory().constructType(collectionType, (Class) null);
            o = or.readValue(jsonString, javaType);
        }
        return (T) o;
    }

    /**
     * Returns the Parameterized type for given Raw Type and its corresponding type arguments.
     * Raw type is 'Class' while type arguments are the generic parameters to this class.
     * E.g. AttachmentEntity<P, C> -> AttachmentEntity<User, Role> -> AttachmentEntity.class is raw type P, C are type arguments
     *
     * @param rawType             'Class' of the entity
     * @param actualTypeArguments Generic parametric arguments to this class
     * @param ownerType           Owner type of this parameterized type
     * @return Parameterized type for given Raw Type and its corresponding type arguments
     */
    public static Type getParameterizedType(final Type rawType, final Type[] actualTypeArguments, final Type ownerType) {
        return new ParameterizedType() {
            public Type[] getActualTypeArguments() {
                return actualTypeArguments;
            }

            public Type getRawType() {
                return rawType;
            }

            public Type getOwnerType() {
                return ownerType;
            }
        };
    }

    public JsonFactory getJsonFactory() {
        return jsonFactory;
    }

    public static String javaToJSON(Object o) throws Exception {
        String jsonReply = null;
        if (o != null) {
            ObjectWriter ow = new ObjectMapper().writer();
            try {
                jsonReply = ow.writeValueAsString(o);
            } catch (Exception e) {
                throw e;
            }
        }
        return jsonReply;
    }

    public static <T> List<T> asList(T... object) {
        if (object != null) {
            List<T> list = new ArrayList<T>(object.length);
            addInCollection(list, object);
            return list;
        }
        return null;
    }

    public static <T> void addInCollection(Collection<T> collection, T[] objects) {
        if (objects != null && collection != null) {
            for (T data : objects) {
                collection.add(data);
            }
        }
    }
}
