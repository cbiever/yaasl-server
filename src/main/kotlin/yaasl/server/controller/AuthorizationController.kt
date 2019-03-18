package yaasl.server.controller

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import yaasl.server.security.SecurityConstants.TOKEN_PREFIX
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/rs/authorizations")
class AuthorizationController {

    private val LOG = LoggerFactory.getLogger(javaClass)

    @ApiOperation(value = "getAuthorizations", nickname = "getAuthorizations")
    @ApiResponses(value = arrayOf(
            ApiResponse(code = 200, message = "Success", response = List::class),
            ApiResponse(code = 403, message = "Forbidden")))
    @GetMapping(produces = [APPLICATION_JSON_UTF8_VALUE])
    fun getAuthorizations(@CookieValue("yaasl") token: String?): ResponseEntity<List<String>> {
        val headers = HttpHeaders()
        headers.add(AUTHORIZATION, TOKEN_PREFIX + token)
        return ResponseEntity(SecurityContextHolder.getContext().authentication.authorities.map { authority -> authority.authority }, headers, OK)
    }

    @ApiOperation(value = "logoff", nickname = "logoff")
    @ApiResponses(value = arrayOf(
            ApiResponse(code = 200, message = "Success"),
            ApiResponse(code = 403, message = "Forbidden")))
    @PostMapping("/logoff")
    fun logoff(response: HttpServletResponse) {
// attempt to delete cookie triggered by server
//        response.addCookie(Cookie("yaasl", "invalid").apply { maxAge = 0 })
    }

}