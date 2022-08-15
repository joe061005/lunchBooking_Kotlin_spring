package com.project.lunchBooking.service

import com.project.lunchBooking.errorHandler.SuccessResponse
import com.project.lunchBooking.model.User
import com.project.lunchBooking.repo.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.client.HttpClientErrorException
import java.util.*
import javax.servlet.http.*

@Service
class UserService(
    private val repository: UserRepository,
    @Autowired private val jdbc : JdbcTemplate = JdbcTemplate(),
    private val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
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

        user.password = passwordEncoder.encode(user.password)

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

    fun getUserByUsername(username: String): User?{
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

    fun Login(user: User, response: HttpServletResponse): ResponseEntity<SuccessResponse> {

        val existingUser: User = repository.findByUsername(user.username)
            ?: throw IllegalArgumentException("Invalid username or email")

        if(!passwordEncoder.matches(user.password, existingUser.password)){
            throw IllegalArgumentException("Invalid username or email")
        }

        val jwt = Jwts.builder()
            .setIssuer(existingUser.id.toString())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(SignatureAlgorithm.ES512, "userLogin").compact()

        val cookie = Cookie("jwt", jwt)

        // front-end cannot read it
        cookie.isHttpOnly = true

        response.addCookie(cookie)

        return ResponseEntity.ok(SuccessResponse(message = "You have successfully logged in to the system."))

    }


}