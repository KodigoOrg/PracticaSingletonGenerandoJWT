import com.fasterxml.jackson.databind.ObjectMapper; // Importamos la clase principal de la librería Jackson.
// Es la encargada de convertir objetos Java a JSON y viceversa.
import java.util.Map; // Necesario para trabajar con Maps, que es la estructura que usaremos
// para representar los objetos JSON en Java.

/**
 * Clase de utilidad para la serialización y deserialización de JSON.
 * Actúa como un puente entre los objetos Java (especialmente Mapas) y el formato JSON.
 *
 * Utiliza la librería 'Jackson', que es una de las más populares y eficientes
 * en el ecosistema de Java para manejar JSON.
 */
public class JsonUtil {

    // --- Instancia de ObjectMapper ---
    // 'ObjectMapper' es la clase central de Jackson. Se encarga de realizar todas
    // las operaciones de mapeo entre JSON y objetos Java.
    // Se declara como 'static final' porque:
    // - 'static': Queremos que haya una única instancia de ObjectMapper para toda la aplicación.
    //             Esto es eficiente porque su creación puede ser costosa y no cambia su estado.
    // - 'final': Una vez que se asigna la instancia, no se puede cambiar.
    private static final ObjectMapper mapper = new ObjectMapper();

    // --- Método para Convertir Objeto Java a JSON (Serialización) ---
    /**
     * Convierte un objeto Java (representado como un Map) a su cadena JSON correspondiente.
     * Este proceso se llama **serialización**.
     *
     * @param data El Map de Java que contiene las claves y valores que se desean convertir a JSON.
     * Por ejemplo, un Map como: {"nombre": "Juan", "edad": 30}
     * @return Una cadena que representa el objeto JSON.
     * Ej: {"nombre":"Juan","edad":30}
     * @throws RuntimeException Si ocurre algún error durante el proceso de conversión a JSON.
     */
    public static String toJson(Map<String, Object> data) {
        try {
            // mapper.writeValueAsString(data):
            // Este es el método mágico de Jackson. Toma nuestro 'Map<String, Object>'
            // y lo transforma automáticamente en una cadena de texto con formato JSON.
            // Jackson maneja internamente la complejidad de formatear el JSON,
            // escapar caracteres especiales, etc.
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            // Manejo de Excepciones:
            // Si algo sale mal durante la escritura del JSON (ej. problemas de E/S,
            // objetos complejos que Jackson no puede manejar por defecto),
            // se captura la excepción y se lanza una RuntimeException.
            // En una aplicación real, probablemente harías un manejo de errores más específico
            // o loggearías el error detalladamente.
            throw new RuntimeException("Error al convertir Map a JSON", e);
        }
    }

    // --- Método para Convertir JSON a Objeto Java (Deserialización) ---
    /**
     * Convierte una cadena JSON a un objeto Java (representado como un Map).
     * Este proceso se llama **deserialización**.
     *
     * @param json La cadena JSON que se desea convertir a un Map de Java.
     * Ej: "{\"nombre\":\"Juan\",\"edad\":30}"
     * @return Un Map de Java que representa el objeto JSON.
     * Ej: {nombre=Juan, edad=30}
     * @throws RuntimeException Si ocurre algún error durante el proceso de conversión de JSON.
     */
    public static Map fromJson(String json){
        try {
            // mapper.readValue(json, Map.class):
            // Este es el otro método clave de Jackson. Toma la cadena 'json'
            // y la parsea (analiza) para convertirla en un objeto Java.
            // Le decimos a Jackson que queremos que el resultado sea un 'Map.class'
            // (es decir, una instancia de Map).
            // Jackson es lo suficientemente inteligente como para manejar los tipos de datos
            // dentro del JSON (strings, números, booleanos, objetos anidados, arrays)
            // y mapearlos a los tipos Java correspondientes dentro del Map.
            return mapper.readValue(json, Map.class);
        } catch (Exception e) {
            // Manejo de Excepciones:
            // Si la cadena JSON no es válida, está mal formada o hay otros problemas
            // durante la lectura, se captura la excepción y se lanza una RuntimeException.
            // Como en toJson(), un manejo más robusto de errores sería ideal en producción.
            throw new RuntimeException("Error al convertir JSON a Map", e);
        }
    }
}