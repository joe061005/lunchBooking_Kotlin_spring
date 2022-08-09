package com.project.lunchBooking.repo

import com.project.lunchBooking.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Int>{

    fun findByUsername(username: String): User;
}