package com.project.lunchBooking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession

@SpringBootApplication
class LunchBookingApplication

fun main(args: Array<String>) {
	runApplication<LunchBookingApplication>(*args)
}
