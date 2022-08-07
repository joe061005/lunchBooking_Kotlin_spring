package com.project.lunchBooking.controller

import com.project.lunchBooking.repo.UserRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class UserController(private val userRepository: UserRepository) {

    @GetMapping
    fun helloWorld(): String{
        return "HELLO"
    }

}