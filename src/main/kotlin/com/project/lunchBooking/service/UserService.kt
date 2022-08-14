package com.project.lunchBooking.service

import com.project.lunchBooking.model.User
import com.project.lunchBooking.repo.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.*

@Service
class UserService(
    private val repository: UserRepository,
    @Autowired private val jdbc : JdbcTemplate = JdbcTemplate()
) {

    fun saveUser(user: User): User{
        if(user.id != -1 || user.verify != false){
            throw IllegalArgumentException("Id must be -1 and verify must be false")
        }

        val queryString = "SELECT * FROM user WHERE username = ? OR email = ?"

        val duplicatedUsers: List<User> = jdbc.query(queryString, BeanPropertyRowMapper(User::class.java), user.username, user.email)

        if(duplicatedUsers.isNotEmpty()){
            throw IllegalArgumentException("username or email exists")
        }

        return repository.save(user)
    }

    fun saveUsers(users: List<User>) : List<User>{
        return repository.saveAll(users)
    }

    fun getUsers(): List<User>{
        return repository.findAll()
    }

    fun getUserById(id: Int): User?{
        return repository.findByIdOrNull(id)
    }

    fun getUserByUsername(username: String): User{
        return repository.findByUsername(username)
    }

    fun deleteUser(id: Int): String{
        repository.deleteById(id);
        return "produce $id deleted !!"
    }

    fun updateUser(user: User): User?{
        var existingUser:User? = repository.findByIdOrNull(user.id)
        if (existingUser != null) {
            existingUser.username = user.username
            existingUser.password = user.password
            existingUser.email = user.email
            existingUser.verify = user.verify
            return repository.save(existingUser)
        }
        return null
    }
}