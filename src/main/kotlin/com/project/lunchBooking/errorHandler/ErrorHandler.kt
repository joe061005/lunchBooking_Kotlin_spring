package com.project.lunchBooking.errorHandler

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ErrorHandler {

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException): ResponseEntity<ErrorResponse>{
        return ResponseEntity.badRequest().body(ErrorResponse(message = ex.localizedMessage))
    }
}