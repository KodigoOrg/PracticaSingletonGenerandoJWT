import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JWTGeneratorMain {
    public static void main(String[] args) {
        JWTManager jwt = JWTManager.getInstance();

        Map<String, Object>claims = new HashMap<>();

        claims.put("sub", "Usuario123");

        String token = jwt.generateToken(claims);

        System.out.println("Token generado: " + token);
        boolean valido = jwt.verifyToken(token);

        System.out.println("Token valido? " + valido);
    }
}
