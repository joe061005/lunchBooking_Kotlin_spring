package com.project.lunchBooking.controller

import com.project.lunchBooking.model.User
import com.project.lunchBooking.service.UserService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("api")
class UserController(private val userService: UserService) {

    @PostMapping("/addUser")
    fun addUser(@RequestBody user: User): User{
        return userService.saveUser(user)
    }

    @PostMapping("/addUsers")
    fun addUsers(@RequestBody users: List<User>): List<User>{
        return userService.saveUsers(users)
    }

    @GetMapping("/users")
    fun findAllUsers(): List<User>{
        return userService.getUsers()
    }

    @GetMapping("/userById/{id}")
    fun findUserById(@PathVariable id: Int): User?{
        return userService.getUserById(id)
    }

    @GetMapping("/user/{username}")
    fun findUserByName(@PathVariable username: String): User{
        return userService.getUserByUsername(username)
    }

    @PutMapping("/user/update")
    fun updateUser(@RequestBody user: User): User?{
        return userService.updateUser(user)
    }

    @DeleteMapping("/user/{id}")
    fun deleteUser(@PathVariable id: Int): String{
        return userService.deleteUser(id)
    }





    // val person = this.findPersonById(...) ?: throw IllegalStateException("${..} not found)

}