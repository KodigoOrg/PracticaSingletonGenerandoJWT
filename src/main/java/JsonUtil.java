import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Map<String, Object> data) {
        try{
            return mapper.writeValueAsString(data);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static Map fromJson(String json){
        try{
            return mapper.readValue(json, Map.class);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
