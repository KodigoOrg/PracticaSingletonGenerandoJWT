import java.util.Base64;
import java.util.Map;
import java.util.Objects;

public class JWTManager {
    private static final String SECRET = "mi_clave_secreta_123";
    private static final String ALG = "HS256";
    private static JWTManager instance;

    public JWTManager() {
    }

    public static JWTManager getInstance(){
        if (instance == null){
            instance = new JWTManager();
        }
        return instance;
    }

    public String generateToken(Map<String, Object> claims){
        long iat = System.currentTimeMillis()/1000;

        claims.put("iat", iat);
        if(!claims.containsKey("exp")){
            claims.put("exp", iat + 3600);
        }

        String headerJson = "{\\\"alg\\\":\\\"HS256\\\",\\\"typ\\\":\\\"JWT\\\"}";
        String payloadJson = JsonUtil.toJson(claims);

        String encodeHeader = Base64Url.encode(headerJson.getBytes());
        String encodedPayload = Base64Url.encode(payloadJson.getBytes());
        String content = encodeHeader + "." +encodedPayload;
        String signature = Base64Url.encode(HMAUtil.sing(content, SECRET));

        return content + "." + signature;
    }

    public  boolean verifyToken(String token){
        String[] parts = token.split("\\.");

        if (parts.length != 3) return false;

        String content = parts[0] + "." + parts[1];
        String expectedSig = Base64Url.encode(HMAUtil.sing(content, SECRET));

        if (!expectedSig.equals(parts[2])) return false;

        String payloadJson = new String(Base64Url.decode(parts[1]));
        Map<String, Object> payload = JsonUtil.fromJson(payloadJson);

        long now = System.currentTimeMillis()/1000;

        if (payload.containsKey("exp") &&
                now > ((Number) payload.get("exp")).longValue()){
            return false;
        }
        return true;
    }
}
