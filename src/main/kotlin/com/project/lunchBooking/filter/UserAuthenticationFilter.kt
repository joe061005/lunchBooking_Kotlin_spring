package com.project.lunchBooking.filter

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.authentication.AuthenticationManager

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class UserAuthenticationFilter(private val authManager: AuthenticationManager) : UsernamePasswordAuthenticationFilter(){

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
            .signWith(SignatureAlgorithm.ES512, "userLogin")
            .setSubject(request.requestURI.toString())
            .claim("roles", user.authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .compact()
        val refresh_token: String = Jwts.builder()
            .setIssuer(user.username)
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(SignatureAlgorithm.ES512, "userLogin")
            .setSubject(request.requestURI.toString())
            .compact()
        response.setHeader("access_token", access_token)
        response.setHeader("refresh_token", refresh_token)

    }
}