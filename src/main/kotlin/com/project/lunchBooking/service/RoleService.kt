package com.project.lunchBooking.service

import com.project.lunchBooking.model.Role
import com.project.lunchBooking.model.User
import com.project.lunchBooking.repo.RoleRepository
import com.project.lunchBooking.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class RoleService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    @Autowired private val jdbc : JdbcTemplate = JdbcTemplate(),
) {

    fun saveRole(role: Role): Role {
        if(role.id != -1){
            throw IllegalArgumentException("Id must be -1")
        }

        val queryString = "SELECT * FROM role WHERE name = ?"

        val duplicatedRoles: List<Role> = jdbc.query(queryString, BeanPropertyRowMapper(Role::class.java), role.name)

        if(duplicatedRoles.isNotEmpty()){
            throw IllegalArgumentException("role exists")
        }

        return roleRepository.save(role)
    }

    fun addRoleToUser(username: String, roleName: String): User {
        val user: User = userRepository.findByUsername(username) ?: throw IllegalArgumentException("Invalid username")
        val role: Role = roleRepository.findByName(roleName) ?: throw IllegalArgumentException("Invalid role")
        if (user.roles!!.contains(role)){
            throw IllegalArgumentException("role exists")
        }
        user.roles!!.add(role)
        userRepository.save(user)
        return user
    }
}