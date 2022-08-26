package com.project.lunchBooking.repo

import com.project.lunchBooking.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepository: JpaRepository<User, Int>{

    fun findByUsername(username: String): User?

    // use entity class name instead of table name
    // instruct Spring that it can modify a record in DB
    @Query("UPDATE User SET failed_attempt = ?1 WHERE username= ?2")
    @Modifying
    @Transactional
    fun updateFailedAttempt(failedAttempt: Int, username: String)




}