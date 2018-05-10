package yaasl.server.security;

public class SecurityConstants {

    public static final long EXPIRATION_TIME = 10 * 60 * 60 * 1000;
    public static final int REMEMBER_ME_EXPIRATION_TIME = 10 * 24 * 60 * 60;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String WEB_SOCKET_TOKEN_HEADER = "Sec-WebSocket-Protocol";
    public static final String SIGN_UP_URL = "/login";
    public static final String REMEMBER_ME_COOKIE = "yaasl-remember-me";

}
