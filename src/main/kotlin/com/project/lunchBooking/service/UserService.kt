package com.project.lunchBooking.service

import com.project.lunchBooking.model.User
import com.project.lunchBooking.repo.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(private val repository: UserRepository) {

    fun saveUser(user: User): User{
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