package com.project.lunchBooking.security

import com.project.lunchBooking.filter.UserAuthenticationFilter
import com.project.lunchBooking.filter.UserAuthorizationFilter
import com.project.lunchBooking.securityHandler.UserAuthenticationEntryPoint
import com.project.lunchBooking.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authManagerBuilder: AuthenticationManagerBuilder,
    private val userService: UserService
) {


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

//    @Bean
//    fun corsConfigurationSource(): CorsConfigurationSource {
//        val config = CorsConfiguration()
//        config.allowedOrigins = listOf("http://localhost:8080")
//        config.allowedMethods = listOf("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH")
//        config.allowCredentials = true
//        config.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type")
//        val source = UrlBasedCorsConfigurationSource()
//        source.registerCorsConfiguration("/**", config)
//        return source
//    }

    // use UserAuthenticationFilter to do user verification (endpoint: xx/login)
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        // set login endpoint
        val userAuthenticationFilter = UserAuthenticationFilter(authManagerBuilder.orBuild, userService)
        userAuthenticationFilter.setFilterProcessesUrl("/api/v1/user/login")

        // prevent CSRF attack using cookie
        http.csrf().disable()

        // don't use cookie
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        // use .permitAll() for public access
        http.authorizeRequests().antMatchers("/api/v1/user/login/**", "/api/v1/token/refresh/**", "/api/v1/user/addUser/**", "/api/v1/user/emailVerification/**").permitAll()
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/v1/role/**").hasAnyAuthority("ROLE_ADMIN")
        http.authorizeRequests().anyRequest().authenticated()
        http.exceptionHandling().authenticationEntryPoint(UserAuthenticationEntryPoint())
        http.addFilter(userAuthenticationFilter)
        http.addFilterBefore(UserAuthorizationFilter(), UsernamePasswordAuthenticationFilter::class.java)

        // exclude OPTIONS requests from authorization checks
        http.cors()

        return http.build()

    }


}