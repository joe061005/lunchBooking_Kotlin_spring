package com.project.lunchBooking.errorHandler

import java.time.LocalDateTime

data class SuccessResponse(
    val title: String = "Success",
    val message: String,
    val dateTime: LocalDateTime = LocalDateTime.now()
)