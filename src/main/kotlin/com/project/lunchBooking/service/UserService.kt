package com.project.lunchBooking.service

import com.project.lunchBooking.errorHandler.SuccessResponse
import com.project.lunchBooking.model.Role
import com.project.lunchBooking.model.User
import com.project.lunchBooking.repo.RoleRepository
import com.project.lunchBooking.repo.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.client.HttpClientErrorException
import java.util.*
import javax.servlet.http.*
import kotlin.collections.ArrayList

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    @Autowired private val jdbc : JdbcTemplate = JdbcTemplate(),
    @Lazy private val passwordEncoder: PasswordEncoder

) : UserDetailsService{

    // load the user from DB first and then do verification (used by spring security)
    // override the function in UserDetailService interface
    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepository.findByUsername(username) ?: throw UsernameNotFoundException("user not found in the database")
        val authorities: MutableCollection<SimpleGrantedAuthority> = ArrayList<SimpleGrantedAuthority>()
        user.roles.forEach { authorities.add(SimpleGrantedAuthority(it.name)) }
        return org.springframework.security.core.userdetails.User(user.username, user.password, authorities)
    }

    fun saveRole(role: Role): Role{
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

    fun addRoleToUser(username: String, roleName: String){
        val user: User? = userRepository.findByUsername(username) ?: throw IllegalArgumentException("Invalid username")
        val role: Role? = roleRepository.findByName(roleName) ?: throw IllegalArgumentException("Invalid role")
        user!!.roles.add(role!!)

    }

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

        userRepository.save(user)

        addRoleToUser(user.username, "ROLE_ADMIN")

        return user
    }

    fun saveUsers(users: List<User>) : List<User>{
        return userRepository.saveAll(users)
    }

    fun getUsers(): List<User>{
        return userRepository.findAll()
    }

    fun getUserById(id: Int): User?{
        return userRepository.findByIdOrNull(id)
    }

    fun getUserByUsername(username: String): User?{
        return userRepository.findByUsername(username)
    }

    fun deleteUser(id: Int): String{
        userRepository.deleteById(id);
        return "produce $id deleted !!"
    }

    fun updateUser(user: User): User?{
        var existingUser:User? = userRepository.findByIdOrNull(user.id)
        if (existingUser != null) {
            existingUser.username = user.username
            existingUser.password = user.password
            existingUser.email = user.email
            existingUser.verify = user.verify
            return userRepository.save(existingUser)
        }
        return null
    }

//    fun Login(user: User, response: HttpServletResponse): ResponseEntity<SuccessResponse> {
//
//        val existingUser: User = userRepository.findByUsername(user.username)
//            ?: throw IllegalArgumentException("Invalid username or email")
//
//        if(!passwordEncoder.matches(user.password, existingUser.password)){
//            throw IllegalArgumentException("Invalid username or email")
//        }
//
//        val jwt = Jwts.builder()
//            .setIssuer(existingUser.id.toString())
//            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60))
//            .signWith(SignatureAlgorithm.ES512, "userLogin").compact()
//
//        val cookie = Cookie("jwt", jwt)
//
//        // front-end cannot read it
//        cookie.isHttpOnly = true
//
//        response.addCookie(cookie)
//
//        return ResponseEntity.ok(SuccessResponse(message = "You have successfully logged in to the system."))
//
//    }




}