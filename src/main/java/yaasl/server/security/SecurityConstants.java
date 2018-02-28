package yaasl.server.security;

public class SecurityConstants {

    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String SECRET = "ThisIsASecret";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String WEB_SOCKET_TOKEN_HEADER = "Sec-WebSocket-Protocol";
    public static final String SIGN_UP_URL = "/login";
}