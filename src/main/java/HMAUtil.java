import javax.crypto.Mac; // Necesario para la clase Mac, que se usa para calcular HMAC
import javax.crypto.spec.SecretKeySpec; // Necesario para especificar la clave secreta
import java.security.InvalidKeyException; // Excepción para claves inválidas
import java.security.NoSuchAlgorithmException; // Excepción si el algoritmo no existe

/**
 * Clase de utilidad para realizar operaciones de firma HMAC.
 * HMAC (Hash-based Message Authentication Code) es un tipo de código de autenticación
 * de mensajes que utiliza una función hash criptográfica (como SHA-256) y una clave secreta.
 * Se usa para verificar la integridad de los datos y la autenticidad del remitente.
 */
public class HMAUtil {

    /**
     * Calcula una firma HMAC-SHA256 para una cadena de datos dada y una clave secreta.
     *
     * @param data La cadena de texto (los "datos") que se quiere firmar.
     * @param secret La clave secreta (una cadena de texto) que solo el remitente y el receptor conocen.
     * Esta clave es crucial para la seguridad de la firma.
     * @return Un arreglo de bytes que representa la firma HMAC calculada.
     * @throws RuntimeException Si ocurre un error al inicializar el algoritmo HMAC
     * (por ejemplo, si el algoritmo no está disponible o la clave es inválida).
     */
    public static byte[] sing(String data, String secret) {
        try {
            // 1. Obtener una instancia del generador de HMAC con el algoritmo SHA-256.
            // "HmacSHA256" indica que usaremos HMAC junto con la función hash SHA-256.
            // La clase 'Mac' es parte del Java Cryptography Architecture (JCA).
            Mac hmac = Mac.getInstance("HmacSHA256");

            // 2. Crear una especificación de clave secreta.
            // Convertimos nuestra 'secret' (String) a bytes, ya que los algoritmos criptográficos
            // trabajan con bytes. El segundo parámetro indica el algoritmo para el que se usará esta clave.
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");

            // 3. Inicializar el objeto HMAC con nuestra clave secreta.
            // Esto "prepara" el algoritmo HMAC para que sepa qué clave usar al calcular la firma.
            hmac.init(keySpec);

            // 4. Calcular la firma HMAC final.
            // Convertimos los 'data' (String) a bytes y pasamos estos bytes al método doFinal().
            // Este método ejecuta la función hash SHA-256 sobre los datos, mezclándolos
            // con la clave secreta de una manera criptográficamente segura.
            // El resultado es la firma HMAC, que es un arreglo de bytes.
            return  hmac.doFinal(data.getBytes());

        } catch (NoSuchAlgorithmException e) {
            // Captura esta excepción si el algoritmo "HmacSHA256" no está disponible
            // en el entorno Java. Esto es extremadamente raro en Java moderno.
            System.err.println("Error: Algoritmo HMAC-SHA256 no encontrado. " + e.getMessage());
            throw new RuntimeException("Error firmando: Algoritmo no disponible", e);
        } catch (InvalidKeyException e) {
            // Captura esta excepción si la clave secreta proporcionada es inválida
            // para el algoritmo HMAC-SHA256. Por ejemplo, si la clave tiene un formato incorrecto.
            System.err.println("Error: Clave secreta inválida para HMAC. " + e.getMessage());
            throw new RuntimeException("Error firmando: Clave inválida", e);
        }
    }
}