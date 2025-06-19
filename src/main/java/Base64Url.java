import java.util.Base64; // Importamos la clase Base64, que es parte de Java estándar desde la versión 8.
// Esta clase proporciona todas las herramientas para trabajar con codificación Base64.

/**
 * Clase de utilidad para codificar y decodificar cadenas utilizando el formato Base64 URL-safe.
 *
 * El formato Base64 es una forma de convertir datos binarios (como imágenes, audio, o cualquier secuencia de bytes)
 * en una cadena de texto ASCII. Esto es útil para transmitir datos a través de medios que están diseñados para texto,
 * como URLs, correo electrónico o documentos JSON.
 *
 * La variante "URL-safe" es importante porque los caracteres '+' y '/' (que son parte del Base64 estándar)
 * tienen un significado especial en las URLs y pueden causar problemas si no se manejan correctamente.
 * El Base64 URL-safe los reemplaza por '-' y '_' respectivamente. Además, elimina el relleno '='.
 */
public class Base64Url {

    /**
     * Codifica un arreglo de bytes en una cadena Base64 URL-safe.
     *
     * @param bytes Los datos binarios (como un arreglo de bytes) que se desean codificar.
     * @return Una cadena de texto que representa los datos codificados en formato Base64 URL-safe.
     */
    public static String encode(byte[] bytes) {
        // Base64.getUrlEncoder(): Obtiene un codificador Base64 diseñado específicamente
        // para ser "URL and Filename safe". Esto significa que reemplazará:
        //   - El carácter '+' por '-'
        //   - El carácter '/' por '_'
        //
        // .withoutPadding(): Por defecto, Base64 añade caracteres de relleno '=' al final
        // de la cadena si los datos de entrada no son un múltiplo de 3 bytes.
        // En JWTs y otros contextos de URL, este relleno suele eliminarse para hacer la cadena
        // más compacta y realmente "segura para URL".
        //
        // .encodeToString(bytes): Toma nuestro arreglo de bytes de entrada y realiza la codificación,
        // devolviendo el resultado como una String.
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Decodifica una cadena Base64 URL-safe de nuevo a su arreglo de bytes original.
     *
     * @param part La cadena de texto codificada en Base64 URL-safe.
     * @return Un arreglo de bytes que representa los datos originales antes de la codificación.
     */
    public static byte[] decode(String part) {
        // Base64.getUrlDecoder(): Obtiene un decodificador Base64 que sabe cómo manejar
        // las cadenas codificadas con el formato "URL and Filename safe" (es decir,
        // las que usan '-' y '_' en lugar de '+' y '/').
        //
        // .decode(part): Toma la cadena codificada y la transforma de nuevo en el arreglo de bytes original.
        // Es importante que la cadena de entrada haya sido codificada previamente con un método compatible
        // (como el `encode` de esta misma clase o cualquier otro codificador Base64 URL-safe).
        return Base64.getUrlDecoder().decode(part);
    }
}