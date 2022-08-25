package com.project.lunchBooking.securityHandler

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import java.time.LocalDateTime
import java.util.HashMap
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class UserAuthenticationEntryPoint: AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpStatus.FORBIDDEN.value()
        val error: MutableMap<String, String> = HashMap<String, String>()
        error["timestamp"] = LocalDateTime.now().toString()
        error["status"] = HttpStatus.FORBIDDEN.value().toString()
        error["error"] = HttpStatus.FORBIDDEN.toString()
        error["path"] = request.requestURI
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response.outputStream, error)
    }
}