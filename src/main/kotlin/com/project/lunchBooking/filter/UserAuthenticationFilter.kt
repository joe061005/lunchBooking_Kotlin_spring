package com.project.lunchBooking.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class UserAuthenticationFilter(private val authManager: AuthenticationManager) : UsernamePasswordAuthenticationFilter(){

    // call loadUserByUsername(username) in UserService first to get the UserDetails
    // Then, do the verification
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val username: String = request.getParameter("username")
        val password: String = request.getParameter("password")
        val authenticationToken =
            UsernamePasswordAuthenticationToken(username, password)

        return authManager.authenticate(authenticationToken)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authentication: Authentication
    ) {

        val user: User = authentication.principal as User

        // in production env, the key is stored in other places for security reasons
        val access_token: String = Jwts.builder()
            .setIssuer(user.username)
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 30))
            .signWith(SignatureAlgorithm.HS512, "userLogin")
            .setSubject(request.requestURI.toString())
            .claim("roles", user.authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .compact()
        val refresh_token: String = Jwts.builder()
            .setIssuer(user.username)
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(SignatureAlgorithm.HS512, "userLogin")
            .setSubject(request.requestURI.toString())
            .compact()
        val tokens: MutableMap<String, String> = HashMap<String, String>()
        tokens["access_token"] = access_token
        tokens["refresh_token"] = refresh_token
        response.contentType = APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response.outputStream, tokens)

    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        SecurityContextHolder.clearContext()
        response.status = HttpStatus.UNAUTHORIZED.value()
        val error: MutableMap<String, String> = HashMap<String, String>()
        error["timestamp"] = LocalDateTime.now().toString()
        error["status"] = HttpStatus.UNAUTHORIZED.value().toString()
        error["error"] = HttpStatus.UNAUTHORIZED.toString()
        error["path"] = request.requestURI
        response.contentType = APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response.outputStream, error)
    }
}