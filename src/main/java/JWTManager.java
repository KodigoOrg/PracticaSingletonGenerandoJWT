import javax.crypto.Mac; // Necesario para la clase Mac, que se usa para calcular HMAC
import javax.crypto.spec.SecretKeySpec; // Necesario para especificar la clave secreta
import java.security.InvalidKeyException; // Excepción para claves inválidas
import java.security.NoSuchAlgorithmException; // Excepción si el algoritmo no existe
import java.util.Map; // Para manejar las "claims" del JWT como un mapa de clave-valor

/**
 * Clase de utilidad para gestionar JSON Web Tokens (JWT).
 * Implementa el patrón Singleton para asegurar una única instancia.
 * Se encarga de generar y verificar la validez de los JWTs.
 */
public class JWTManager {
    // --- 1. Constantes y Variables de Instancia ---
    // SECRET: ¡IMPORTANTE! Esta es la clave secreta que solo tu servidor debe conocer.
    // Se usa para firmar y verificar los tokens.
    // En una aplicación real, NUNCA debe estar hardcodeada así.
    // Debe ser una cadena muy larga y compleja, almacenada de forma segura
    // (por ejemplo, en variables de entorno o un servicio de gestión de secretos).
    private static final String SECRET = "mi_clave_secreta_123_muy_segura_y_larga";

    // ALG: Algoritmo de firma utilizado. "HS256" significa HMAC con SHA-256.
    // Debe coincidir con el que se usa en HMAUtil.
    private static final String ALG = "HS256";

    // instance: Variable estática para la única instancia de la clase (Patrón Singleton).
    private static JWTManager instance;

    // --- 2. Constructor (Privado) ---
    // El constructor es privado. Esto es clave para el patrón Singleton,
    // asegurando que la clase no se pueda instanciar directamente desde fuera.
    private JWTManager() {
    }

    // --- 3. Método para Obtener la Instancia (Singleton) ---
    /**
     * Obtiene la única instancia de JWTManager. Si no existe, la crea.
     * Esto asegura que solo haya un punto de control para la gestión de JWTs
     * en toda la aplicación.
     * @return La única instancia de JWTManager.
     */
    public static JWTManager getInstance(){
        if (instance == null){ // Si la instancia aún no ha sido creada
            instance = new JWTManager(); // Crea la instancia por primera vez
        }
        return instance; // Devuelve la instancia existente (o la recién creada)
    }

    // --- 4. Método para Generar un JWT ---
    /**
     * Genera un nuevo JSON Web Token (JWT) a partir de un conjunto de 'claims' (declaraciones/datos).
     * El JWT incluirá un tiempo de emisión ('iat') y, si no se especifica, un tiempo de expiración ('exp') por defecto.
     *
     * @param claims Un mapa que contiene los datos personalizados que se incluirán en el payload del JWT.
     * Ej: {"userId": 123, "role": "admin"}
     * @return La cadena JWT completa en formato compacto (header.payload.signature).
     */
    public String generateToken(Map<String, Object> claims){
        // 'iat' (issued at): Tiempo en segundos desde la época Unix en que el token fue emitido.
        // Se calcula a partir del tiempo actual en milisegundos y se divide por 1000.
        long iat = System.currentTimeMillis()/1000;

        // Añade la claim 'iat' al mapa de claims.
        claims.put("iat", iat);

        // Si el mapa de claims NO contiene una claim 'exp' (expiration time), se añade una por defecto.
        // 'exp' es el tiempo en segundos desde la época Unix en que el token expirará.
        // Aquí se establece para expirar en 3600 segundos (1 hora) desde su emisión.
        if(!claims.containsKey("exp")){
            claims.put("exp", iat + 3600); // 3600 segundos = 1 hora
        }

        // --- Construyendo la CABECERA (Header) del JWT ---
        // La cabecera base del JWT. Es un JSON que describe el algoritmo de firma (HS256) y el tipo (JWT).
        // Las barras invertidas dobles ('\\\"') son necesarias para escapar las comillas dobles
        // dentro de la cadena JSON en Java.
        String headerJson = "{\\\"alg\\\":\\\"HS256\\\",\\\"typ\\\":\\\"JWT\\\"}";

        // --- Construyendo la CARGA ÚTIL (Payload) del JWT ---
        // Convierte el mapa de 'claims' (nuestros datos) a una cadena JSON.
        // Se asume la existencia de una utilidad 'JsonUtil' que tiene un método 'toJson()'.
        // (Esta clase 'JsonUtil' no está incluida aquí, pero sería necesaria para que funcione).
        String payloadJson = JsonUtil.toJson(claims);

        // --- Codificando Header y Payload en Base64Url ---
        // Base64Url es una variante de Base64 que es segura para usar en URLs y nombres de archivo
        // (reemplaza '+' por '-' y '/' por '_' y elimina el relleno '=').
        // Se asume la existencia de una utilidad 'Base64Url' que tiene un método 'encode()'
        // y 'decode()'. (Esta clase 'Base64Url' tampoco está incluida aquí).
        String encodeHeader = Base64Url.encode(headerJson.getBytes());
        String encodedPayload = Base64Url.encode(payloadJson.getBytes());

        // 'content' es la parte que se va a firmar. Consiste en el header codificado,
        // un punto de separación, y el payload codificado.
        String content = encodeHeader + "." + encodedPayload;

        // --- Generando la FIRMA (Signature) del JWT ---
        // Este es el paso crucial de seguridad. Se utiliza la clase 'HMAUtil'
        // (que se explicó previamente) para calcular el HMAC-SHA256 del 'content'
        // usando nuestra 'SECRET' (clave secreta).
        // El resultado (que es un arreglo de bytes) se codifica de nuevo en Base64Url.
        String signature = Base64Url.encode(HMAUtil.sing(content, SECRET));

        // Finalmente, se une todo para formar el JWT completo en su formato compacto:
        // header_codificado.payload_codificado.firma_codificada
        return content + "." + signature;
    }

    // --- 5. Método para Verificar un JWT ---
    /**
     * Verifica la validez de un JSON Web Token (JWT) dado.
     * Este método realiza las siguientes comprobaciones de seguridad y validez:
     * 1. Valida la estructura del token (debe tener exactamente 3 partes separadas por puntos).
     * 2. Verifica la firma del token para asegurar que no ha sido alterado y fue emitido por nuestro servidor.
     * 3. Comprueba el tiempo de expiración ('exp') si está presente en el payload.
     *
     * @param token La cadena JWT completa a verificar.
     * @return {@code true} si el token es válido (firma correcta y no expirado), {@code false} en caso contrario.
     */
    public  boolean verifyToken(String token){
        // Divide el token en sus tres partes usando el punto como delimitador.
        String[] parts = token.split("\\.");

        // Comprueba si el token tiene el formato correcto (exactamente 3 partes).
        // Si no, el token es inválido.
        if (parts.length != 3) return false;

        // Reconstruye el 'content' original que fue usado para generar la firma.
        // Es la unión del header y el payload codificados, separados por un punto.
        String content = parts[0] + "." + parts[1];

        // --- Verificación de la FIRMA (la parte más crítica de seguridad) ---
        // Recalcula la firma esperada usando el 'content' y nuestra misma 'SECRET' (clave secreta).
        // Si el 'content' o la 'SECRET' no son los mismos que se usaron para generar el token,
        // la firma recalculada será diferente.
        String expectedSig = Base64Url.encode(HMAUtil.sing(content, SECRET));

        // Compara la firma que acabamos de recalcular ('expectedSig') con la firma
        // que venía en el token ('parts[2]').
        // Si no coinciden, el token ha sido alterado o no fue firmado con nuestra clave secreta.
        if (!expectedSig.equals(parts[2])) return false;

        // --- Verificación de la Expiración (Payload) ---
        // Decodifica la segunda parte del token (el Payload, que está en Base64Url) a bytes,
        // y luego convierte esos bytes a una cadena JSON.
        String payloadJson = new String(Base64Url.decode(parts[1]));

        // Convierte la cadena JSON del payload de nuevo a un mapa de clave-valor.
        // Se asume la existencia de una utilidad 'JsonUtil' con un método 'fromJson()'.
        Map<String, Object> payload = JsonUtil.fromJson(payloadJson);

        // Obtiene el tiempo actual en segundos desde la época Unix.
        long now = System.currentTimeMillis()/1000;

        // Comprueba si el payload contiene una claim 'exp' (tiempo de expiración).
        // Si la tiene, verifica si el tiempo actual es mayor que el tiempo de expiración.
        // Si 'now' es mayor que 'exp', el token ha expirado y no es válido.
        if (payload.containsKey("exp") &&
                now > ((Number) payload.get("exp")).longValue()){
            return false; // El token ha expirado
        }

        // Si todas las comprobaciones pasan (firma válida y no expirado), el token es válido.
        return true;
    }

    // --- Nuevo Método: Obtener tiempo restante para la expiración ---
    /**
     * Calcula el tiempo restante en segundos hasta que un token JWT expire.
     * Este método primero verifica la autenticidad de la firma del token.
     * Si el token no tiene una claim 'exp', o la firma es inválida, devuelve 0.
     * Si ya ha expirado, devuelve un valor negativo que indica cuántos segundos han pasado desde la expiración.
     *
     * @param token La cadena JWT completa a analizar.
     * @return El número de segundos que le quedan al token para expirar.
     * Devuelve 0 si ya expiró, es inválido (firma errónea o formato incorrecto),
     * o no tiene la claim 'exp'.
     * Devuelve un valor negativo si ya expiró (por ejemplo, -10 si expiró hace 10 segundos).
     */
    public long getTimeUntilExpirationInSeconds(String token) {
        String[] parts = token.split("\\.");

        // 1. Validar la estructura básica del token (Header.Payload.Signature).
        if (parts.length != 3) {
            System.out.println("DEBUG: Token mal formado (no tiene 3 partes).");
            return 0; // Token mal formado
        }

        // 2. --- Verificación de la firma (paso fundamental para la seguridad) ---
        // Antes de confiar en cualquier dato del payload (como la fecha de expiración),
        // debemos asegurar que el token es auténtico y no ha sido alterado.
        String content = parts[0] + "." + parts[1];
        String expectedSig = Base64Url.encode(HMAUtil.sing(content, SECRET));

        if (!expectedSig.equals(parts[2])) {
            System.out.println("DEBUG: Firma del token inválida. Token no auténtico.");
            return 0; // Firma inválida, el token no es auténtico
        }

        // 3. --- Extracción del payload y la claim 'exp' ---
        // Si la firma es válida, procedemos a decodificar y analizar el payload.
        String payloadJson = new String(Base64Url.decode(parts[1]));
        Map<String, Object> payload = JsonUtil.fromJson(payloadJson);

        // 4. Obtener el tiempo de expiración ('exp') del payload.
        if (payload.containsKey("exp")) {
            long expirationTimeSeconds = ((Number) payload.get("exp")).longValue();
            long currentTimeSeconds = System.currentTimeMillis() / 1000;

            // 5. Calcular el tiempo restante.
            // Si el resultado es positivo, son los segundos que quedan.
            // Si es 0 o negativo, el token ya ha expirado.
            return expirationTimeSeconds - currentTimeSeconds;
        } else {
            // Si el token válido no tiene la claim 'exp', para este método
            // que mide "cuánto le falta para morir", devolvemos 0,
            // ya que no hay un tiempo de expiración definido.
            System.out.println("DEBUG: Token válido, pero no contiene la claim 'exp'.");
            return 0;
        }
    }
}
