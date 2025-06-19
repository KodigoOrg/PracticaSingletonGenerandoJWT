# Gestión de JSON Web Tokens (JWT) en Java

Este proyecto proporciona una implementación sencilla para la generación y verificación de JSON Web Tokens (JWT) utilizando Java 8+. Incluye funcionalidades para firmar (HMAC-SHA256), codificar/decodificar Base64 URL-safe, y serializar/deserializar JSON, encapsulando todo esto en una clase JWTManager centralizada.


### Características
 * Generación de JWTs: Crea tokens JWT con cabecera (Header), carga útil (Payload) y firma (Signature).
 * Verificación de JWTs: Valida la autenticidad (firma) e integridad del token, y verifica su tiempo de expiración.
 * Tiempo hasta la Expiración: Calcula cuántos segundos le quedan a un token antes de expirar.
 * HMAC-SHA256: Utiliza HMAC con SHA-256 para asegurar la firma del token.
 * Base64 URL-Safe: Emplea una codificación Base64 compatible con URLs, crucial para JWT.
 * Manejo de JSON: Convierte objetos Java (Mapas) a JSON y viceversa para el payload del token.
 * Patrón Singleton: El JWTManager es implementado como un Singleton, asegurando una única instancia en la aplicación.
## Estructura del Proyecto
El código está organizado en las siguientes clases:
* JWTManager.java: La clase principal que orquesta la creación y verificación de JWTs. Implementa el patrón Singleton.
* HMAUtil.java: Una clase de utilidad para generar firmas HMAC-SHA256.
* JsonUtil.java: Una clase de utilidad para la serialización y deserialización de objetos JSON, utilizando la librería Jackson.
* Base64Url.java: Una clase de utilidad para la codificación y decodificación Base64 URL-safe.
* JWTGeneratorMain.java: Una clase main de ejemplo que demuestra cómo usar el JWTManager.
## Dependencias
Este proyecto utiliza la librería Jackson para el manejo de JSON. Deberás incluirla en tu proyecto. Si usas Maven, agrega la siguiente dependencia a tu pom.xml:

```
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.17.0</version> </dependency>
Si usas Gradle, agrega a tu build.gradle:
```

### Uso
Inicialización del JWTManager
* Obtén la única instancia de JWTManager de la siguiente manera:
```Java
JWTManager jwtManager = JWTManager.getInstance();
```
Generar un Token
 * Para generar un token, crea un Map<String, Object> con las "claims" (declaraciones o datos) que deseas incluir en el payload.

```Java

import java.util.HashMap;
import java.util.Map;

// ...
Map<String, Object> claims = new HashMap<>();
claims.put("sub", "usuario_ejemplo");
claims.put("nombre", "Usuario de Prueba");
claims.put("rol", "admin");

// Opcional: Define un tiempo de expiración específico en segundos Unix
// claims.put("exp", System.currentTimeMillis() / 1000 + 3600); // Expira en 1 hora

String token = jwtManager.generateToken(claims);
System.out.println("Token Generado: " + token);
```
Verificar un Token
* Para verificar si un token es válido (firma correcta y no expirado):

```Java

String receivedToken = "eyJhbGciOiJIUzI1NiIs..."; // Tu token recibido
boolean isValid = jwtManager.verifyToken(receivedToken);
System.out.println("¿El token es válido?: " + isValid);
```
Obtener el Tiempo Restante hasta la Expiración
 * Para saber cuántos segundos le quedan al token para expirar:

```Java

String receivedToken = "eyJhbGciOiJIUzI1NiIs..."; // Tu token recibido
long timeLeftInSeconds = jwtManager.getTimeUntilExpirationInSeconds(receivedToken);

if (timeLeftInSeconds > 0) {
    System.out.println("Al token le quedan " + timeLeftInSeconds + " segundos para expirar.");
} else if (timeLeftInSeconds == 0) {
    System.out.println("El token es válido pero no tiene un tiempo de expiración definido, o ya expiró, o su firma es inválida.");
} else {
    System.out.println("¡ATENCIÓN! El token ha expirado hace " + Math.abs(timeLeftInSeconds) + " segundos.");
}
```
## Notas de Seguridad Importantes
- **Clave Secreta (SECRET)**: La constante SECRET en JWTManager es crítica para la seguridad. En una aplicación de producción, esta clave NUNCA debe estar hardcodeada en el código. Debe ser una cadena muy larga, aleatoria y compleja, almacenada de forma segura (por ejemplo, en variables de entorno, un servicio de gestión de secretos como HashiCorp Vault, o un almacén de claves).
- **No es Encriptación**: JWT es un mecanismo de autenticación e integridad, no de encriptación. La información en el payload está codificada (Base64), no cifrada, y puede ser leída por cualquiera. No incluyas información sensible o confidencial directamente en el payload de un JWT sin un cifrado adicional.
- **Expiración (exp)**: La verificación de la expiración es vital para mitigar ataques de replay. Asegúrate de que tus tokens tengan un tiempo de vida razonable.
Contribuciones
Las contribuciones son bienvenidas. Si tienes alguna mejora o corrección, no dudes en abrir un issue o enviar un pull request.
