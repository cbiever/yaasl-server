package yaasl.server.security

object SecurityConstants {

    val EXPIRATION_TIME = (10 * 60 * 60 * 1000).toLong()
    val REMEMBER_ME_EXPIRATION_TIME = 10 * 24 * 60 * 60
    val TOKEN_PREFIX = "Bearer "
    val SIGN_UP_URL = "/login"
    val REMEMBER_ME_COOKIE = "yaasl-remember-me"

}
