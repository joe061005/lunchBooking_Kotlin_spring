package com.project.lunchBooking.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.lunchBooking.errorHandler.ErrorResponse
import com.project.lunchBooking.service.UserService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.LockedException
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
import com.project.lunchBooking.model.User as appUser


class UserAuthenticationFilter(
    private val authManager: AuthenticationManager,
    private val userService: UserService,
) : UsernamePasswordAuthenticationFilter() {

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

        userService.updateFailedAttempt(user.username)

        // in production env, the key is stored in other places for security reasons
        val accessTokenExpiryTime: Long = System.currentTimeMillis() + 1000 * 60 * 30
        val refreshTokenExpiryTime: Long = System.currentTimeMillis() + 1000 * 60 * 60

        val access_token: String = Jwts.builder()
            .setIssuer(user.username)
            .setExpiration(Date(accessTokenExpiryTime))
            .signWith(SignatureAlgorithm.HS512, "userLogin")
            .setSubject(request.requestURI.toString())
            .claim("roles", user.authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .compact()
        val refresh_token: String = Jwts.builder()
            .setIssuer(user.username)
            .setExpiration(Date(refreshTokenExpiryTime))
            .signWith(SignatureAlgorithm.HS512, "userRefresh")
            .setSubject(request.requestURI.toString())
            .compact()
        val tokens: MutableMap<String, String> = HashMap<String, String>()
        tokens["access_token"] = access_token
        tokens["refresh_token"] = refresh_token
        tokens["access_token_expiry_time"] = accessTokenExpiryTime.toString()
        tokens["refresh_token_expiry_time"] = refreshTokenExpiryTime.toString()
        response.contentType = APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response.outputStream, tokens)

    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        val error: MutableMap<String, String> = HashMap<String, String>()

        val username: String = request.getParameter("username")
        val user: appUser? = userService.getUserByUsername(username)

        if (user != null) {
            if (user.accountNonLocked == true) {
                // add failedAttempt by 1 if it is 0 or 1
                if (user.failedAttempt!! < UserService.MAX_FAILED_ATTEMPTS - 1) {
                    userService.increaseFailedAttempt(user)
                    error["message"] = "Invalid username or password"
                } else { // lock the account if failedAttempt is 2
                    userService.lock(user)
                    error["message"] =
                        "Your account has been locked due to 3 failed attempts. It will be unlocked after 15 minutes (${Date(user.lockTime!!.time + UserService.LOCK_TIME_DURATION)})."
                }
            } else if (user.accountNonLocked == false) {
                // show error message if the account is locked
                error["message"] =
                    "Your account has been locked due to 3 failed attempts. It will be unlocked after 15 minutes (${Date(user.lockTime!!.time + UserService.LOCK_TIME_DURATION)})."
            }
        }else {
            error["message"] = "Invalid username or password"
        }

        SecurityContextHolder.clearContext()
        response.status = HttpStatus.UNAUTHORIZED.value()
        error["timestamp"] = LocalDateTime.now().toString()
        error["status"] = HttpStatus.UNAUTHORIZED.value().toString()
        error["error"] = HttpStatus.UNAUTHORIZED.toString()
        error["path"] = request.requestURI
        response.contentType = APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response.outputStream, error)
    }
}