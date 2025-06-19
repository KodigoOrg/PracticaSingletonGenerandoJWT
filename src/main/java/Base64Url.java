import java.util.Base64;

public class Base64Url {
    public static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static byte[] decode(String part) {
        return Base64.getUrlDecoder().decode(part);
    }
}
