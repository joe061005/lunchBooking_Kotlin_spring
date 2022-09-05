package com.project.lunchBooking.controller



import com.project.lunchBooking.model.User
import com.project.lunchBooking.model.UsernameForm
import com.project.lunchBooking.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/user")
class UserController(
    private val userService: UserService,
) {

    @PostMapping("/addUser")
    fun addUser(@RequestBody user: User): ResponseEntity<User> {
        return ResponseEntity(userService.saveUser(user), HttpStatus.CREATED)
    }

    @PostMapping("/addAdmin")
    fun addAdmin(@RequestBody user: User): ResponseEntity<User> {
        return ResponseEntity(userService.saveAdmin(user), HttpStatus.CREATED)
    }

    @PostMapping("/addUsers")
    fun addUsers(@RequestBody users: List<User>): List<User> {
        return userService.saveUsers(users)
    }

    @GetMapping("/users")
    fun findAllUsers(): List<User> {
        return userService.getUsers()
    }

    @GetMapping("/userById/{id}")
    fun findUserById(@PathVariable id: Int): User? {
        return userService.getUserById(id)
    }

    @GetMapping("/userByName/{username}")
    fun findUserByName(@PathVariable username: String): User? {
        return userService.getUserByUsername(username)
    }

    @PutMapping("/update")
    fun updateUser(@RequestBody user: User): User? {
        return userService.updateUser(user)
    }

    @DeleteMapping("/delete/{id}")
    fun deleteUser(@PathVariable id: Int): String {
        return userService.deleteUser(id)
    }

    @GetMapping("/emailVerification/{token}")
    fun verifyEmail(@PathVariable token: String): ResponseEntity<String>{
        val successful: Boolean = userService.verifyEmail(token)

        return if (successful){
            ResponseEntity.ok("The account has been verified")
        }else{
            ResponseEntity("Invalid token", HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/resendEmail")
    fun resendEmail(@RequestBody form: UsernameForm){

    }




}