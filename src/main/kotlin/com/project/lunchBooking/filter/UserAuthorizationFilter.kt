package com.project.lunchBooking.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType

// for determining if the user has access to the application
class UserAuthorizationFilter: OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        if(request.servletPath.equals("/api/v1/user/login") || request.servletPath.equals("/api/v1/token/refresh")){
            filterChain.doFilter(request, response)
        }else{
            val authorizationHeader: String? = request.getHeader(AUTHORIZATION)
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                try {
                    val token: String = authorizationHeader.substring("Bearer ".length)
                    val body: Claims = Jwts.parser().setSigningKey("userLogin").parseClaimsJws(token).body
                    val username: String = body.issuer.toString()
                    val roles: List<String> = body["roles"] as ArrayList<String>
                    val authorities: MutableCollection<SimpleGrantedAuthority> = ArrayList<SimpleGrantedAuthority>()
                    roles.forEach {
                        authorities.add(SimpleGrantedAuthority(it))
                    }
                    val authenticationToken = UsernamePasswordAuthenticationToken(username, null, authorities)
                    SecurityContextHolder.getContext().authentication = authenticationToken
                    filterChain.doFilter(request, response)
                }catch (exception: Exception){
                    response.setHeader("error", exception.message)
                    response.status = FORBIDDEN.value()
                    val error: MutableMap<String, String> = HashMap<String, String>()
                    error["error_message"] = exception.message.toString()
                    response.contentType = MediaType.APPLICATION_JSON_VALUE
                    ObjectMapper().writeValue(response.outputStream, error)

                }

            }else {
                filterChain.doFilter(request, response)
            }

        }

    }
}