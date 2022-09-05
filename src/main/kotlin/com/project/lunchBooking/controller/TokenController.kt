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
    fun refreshToken(request: HttpServletRequest, response: HttpServletResponse) {
        val authorizationHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                val refresh_token: String = authorizationHeader.substring("Bearer ".length)
                val body: Claims = Jwts.parser().setSigningKey("userRefresh").parseClaimsJws(refresh_token).body

                val username: String = body.issuer.toString()
                val user: User? = userService.getUserByUsername(username) ?: throw RuntimeException("Invalid token")
                val accessTokenExpiryTime: Long = System.currentTimeMillis() + 1000 * 60 * 30
                val refreshTokenExpiryTime: Long = System.currentTimeMillis() + 1000 * 60 * 60
                val access_token: String = Jwts.builder()
                    .setIssuer(user!!.username)
                    .setExpiration(Date(accessTokenExpiryTime))
                    .signWith(SignatureAlgorithm.HS512, "userLogin")
                    .setSubject(request.requestURI.toString())
                    .claim("roles", user.roles!!.stream().map(Role::name).collect(Collectors.toList()))
                    .compact()
                val new_refresh_token: String = Jwts.builder()
                    .setIssuer(user.username)
                    .setExpiration(Date(refreshTokenExpiryTime))
                    .signWith(SignatureAlgorithm.HS512, "userRefresh")
                    .setSubject(request.requestURI.toString())
                    .compact()
                val tokens: MutableMap<String, String> = java.util.HashMap<String, String>()
                tokens["access_token"] = access_token
                tokens["refresh_token"] = new_refresh_token
                tokens["access_token_expiry_time"] = accessTokenExpiryTime.toString()
                tokens["refresh_token_expiry_time"] = refreshTokenExpiryTime.toString()
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                ObjectMapper().writeValue(response.outputStream, tokens)

            } catch (exception: Exception) {
                response.setHeader("error", exception.message)
                response.status = HttpStatus.FORBIDDEN.value()
                val error: MutableMap<String, String> = HashMap<String, String>()
                error["error_message"] = exception.message.toString()
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                ObjectMapper().writeValue(response.outputStream, error)

            }

        } else {
            throw RuntimeException("Refresh token is missing")
        }
    }
}