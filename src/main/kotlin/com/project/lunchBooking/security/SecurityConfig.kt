package com.project.lunchBooking.security

import com.project.lunchBooking.filter.UserAuthenticationFilter
import com.project.lunchBooking.filter.UserAuthorizationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authManagerBuilder: AuthenticationManagerBuilder
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    // use UserAuthenticationFilter to do user verification (endpoint: xx/login)
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        // set login endpoint
        val userAuthenticationFilter = UserAuthenticationFilter(authManagerBuilder.orBuild)
        userAuthenticationFilter.setFilterProcessesUrl("/api/v1/user/login")

        // prevent CSRF attack using cookie
        http.csrf().disable()

        // don't use cookie
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        // use .permitAll() for public access
        http.authorizeRequests().antMatchers("/api/v1/user/login/**").permitAll()
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/v1/role/**").hasAnyAuthority("ROLE_ADMIN")
        http.authorizeRequests().anyRequest().authenticated()
        // http.exceptionHandling().accessDeniedHandler()
        http.addFilter(userAuthenticationFilter)
        http.addFilterBefore(UserAuthorizationFilter(), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()

    }
}