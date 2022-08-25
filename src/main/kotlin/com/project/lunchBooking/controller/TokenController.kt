package com.project.lunchBooking.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.lunchBooking.model.Role
import com.project.lunchBooking.model.User
import com.project.lunchBooking.service.UserService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.HashMap

@RestController
@RequestMapping("api/v1/token")
class TokenController(
    private val userService: UserService
) {

    @GetMapping("/refresh")
    fun refreshToken(request: HttpServletRequest, response: HttpServletResponse,) {
        val authorizationHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try {
                val refresh_token: String = authorizationHeader.substring("Bearer ".length)
                val body: Claims = Jwts.parser().setSigningKey("userLogin").parseClaimsJws(refresh_token).body
                val username: String = body.issuer.toString()
                val user: User? = userService.getUserByUsername(username) ?: throw RuntimeException("Invalid token")
                val access_token: String = Jwts.builder()
                    .setIssuer(user!!.username)
                    .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 30))
                    .signWith(SignatureAlgorithm.HS512, "userLogin")
                    .setSubject(request.requestURI.toString())
                    .claim("roles", user.roles!!.stream().map(Role::name).collect(Collectors.toList()))
                    .compact()
                val tokens: MutableMap<String, String> = java.util.HashMap<String, String>()
                tokens["access_token"] = access_token
                tokens["refresh_token"] = refresh_token
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                ObjectMapper().writeValue(response.outputStream, tokens)
            }catch (exception: Exception){
                response.setHeader("error", exception.message)
                response.status = HttpStatus.FORBIDDEN.value()
                val error: MutableMap<String, String> = HashMap<String, String>()
                error["error_message"] = exception.message.toString()
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                ObjectMapper().writeValue(response.outputStream, error)

            }

        }else {
            throw RuntimeException("Refresh token is missing")
        }
    }
}