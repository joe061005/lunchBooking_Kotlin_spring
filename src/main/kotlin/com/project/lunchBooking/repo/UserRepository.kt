package com.project.lunchBooking.repo

import com.project.lunchBooking.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Int> {
    
}