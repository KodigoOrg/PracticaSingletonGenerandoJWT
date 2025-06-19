import java.util.HashMap;
import java.util.Map;
import java.util.Objects; // Aunque no se usa en este main, es común en imports.

/**
 * Clase principal para demostrar el uso de JWTManager.
 * Genera un token, lo verifica y calcula el tiempo restante hasta su expiración.
 */
public class JWTGeneratorMain {
    public static void main(String[] args) {
        // --- 1. Obtener la instancia del JWTManager (Singleton) ---
        // Usamos getInstance() para obtener la única instancia de nuestra clase JWTManager.
        // Esto es importante porque la clave secreta y la lógica de gestión de JWTs
        // se centralizan en una sola ubicación.
        JWTManager jwtManager = JWTManager.getInstance();

        // --- 2. Preparar las 'claims' (datos) para el token ---
        // Las claims son un mapa de clave-valor que contendrá la información que queremos
        // incrustar en el token. Por ejemplo, un ID de usuario, roles, etc.
        Map<String, Object> claims = new HashMap<>();

        // Agregamos algunas claims de ejemplo.
        claims.put("sub", "Usuario123"); // "sub" (subject) es una claim estándar que indica el sujeto del token.
        claims.put("nombre", "Juan Pérez");
        claims.put("rol", "administrador");

        // Opcional: Puedes establecer un tiempo de expiración personalizado en segundos Unix.
        // Si no lo pones, generateToken añadirá 1 hora por defecto.
        // claims.put("exp", System.currentTimeMillis() / 1000 + 120); // Expira en 120 segundos (2 minutos)

        // --- 3. Generar el Token JWT ---
        // Llamamos al método generateToken() del JWTManager, pasándole nuestras claims.
        // Este método construye el Header, Payload y Signature, y los une en la cadena JWT final.
        String token = jwtManager.generateToken(claims);

        System.out.println("--- Token Generado ---");
        System.out.println("Token: " + token);
        System.out.println("----------------------\n");

        // --- 4. Verificar la Validez del Token ---
        // Usamos verifyToken() para comprobar si el token es auténtico (firma válida)
        // y si no ha expirado.
        boolean isValid = jwtManager.verifyToken(token);
        System.out.println("¿El token es VÁLIDO según verifyToken()?: " + isValid);

        // --- 5. Obtener el Tiempo Restante para la Expiración ---
        // Usamos el nuevo método para saber cuánto tiempo le queda al token.
        long timeLeftInSeconds = jwtManager.getTimeUntilExpirationInSeconds(token);

        System.out.println("\n--- Información de Expiración ---");
        if (timeLeftInSeconds > 0) {
            System.out.println("Al token le quedan " + timeLeftInSeconds + " segundos para expirar.");
            // También puedes mostrarlo en minutos u horas para mayor legibilidad
            System.out.printf("Esto es aproximadamente %.2f minutos (o %.2f horas).\n",
                    (double) timeLeftInSeconds / 60, (double) timeLeftInSeconds / 3600);
        } else if (timeLeftInSeconds == 0) {
            System.out.println("El token es válido pero no tiene un tiempo de expiración definido, o ya expiró, o su firma es inválida.");
        } else { // timeLeftInSeconds < 0
            System.out.println("¡ATENCIÓN! El token ha expirado hace " + Math.abs(timeLeftInSeconds) + " segundos.");
        }
        System.out.println("--------------------------------\n");

        // --- Ejemplos de prueba adicionales ---

        // 1. Token con firma inválida (simulando una alteración)
        System.out.println("--- Probando Token Alterado ---");
        String alteredToken = token + "ALTERED"; // Modificamos el token deliberadamente
        boolean isAlteredValid = jwtManager.verifyToken(alteredToken);
        long alteredTimeLeft = jwtManager.getTimeUntilExpirationInSeconds(alteredToken);
        System.out.println("Token alterado: " + alteredToken);
        System.out.println("¿Token alterado es VÁLIDO?: " + isAlteredValid); // Debería ser false
        System.out.println("Tiempo restante para token alterado: " + alteredTimeLeft + " segundos."); // Debería ser 0
        System.out.println("------------------------------\n");

        // 2. Simular un token expirado (para ver cómo reaccionan los métodos)
        // Para esto, generamos un token con un 'exp' muy cercano al 'iat'
        System.out.println("--- Probando Token Expirado (simulado) ---");
        Map<String, Object> expiredClaims = new HashMap<>();
        expiredClaims.put("user", "expiredUser");
        // Establecer una expiración en el pasado (por ejemplo, 10 segundos antes del tiempo actual)
        expiredClaims.put("exp", System.currentTimeMillis() / 1000 - 10);
        String expiredToken = jwtManager.generateToken(expiredClaims);
        System.out.println("Token (simulado) Expirado: " + expiredToken);

        boolean isExpiredValid = jwtManager.verifyToken(expiredToken);
        long expiredTimeLeft = jwtManager.getTimeUntilExpirationInSeconds(expiredToken);
        System.out.println("¿Token (simulado) expirado es VÁLIDO?: " + isExpiredValid); // Debería ser false
        System.out.println("Tiempo restante para token (simulado) expirado: " + expiredTimeLeft + " segundos."); // Debería ser negativo
        System.out.println("-----------------------------------------\n");
    }
}