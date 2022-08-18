package com.project.lunchBooking.repo

import com.project.lunchBooking.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository: JpaRepository<Role, Int> {

    fun findByName(name: String): Role?
}