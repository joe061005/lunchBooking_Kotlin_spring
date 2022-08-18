package com.project.lunchBooking.errorHandler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpClientErrorException

@ControllerAdvice
class ErrorHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse>{
        return ResponseEntity.badRequest().body(ErrorResponse(message = ex.localizedMessage))
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized::class)
    fun handleHttpClientUnauthorizedError(ex: HttpClientErrorException.Unauthorized): ResponseEntity<ErrorResponse>{
        return ResponseEntity(ErrorResponse(message = ex.localizedMessage), HttpStatus.UNAUTHORIZED)
    }


}