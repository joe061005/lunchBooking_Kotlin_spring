package com.project.lunchBooking.controller

import com.project.lunchBooking.errorHandler.SuccessResponse
import com.project.lunchBooking.model.Role
import com.project.lunchBooking.model.RoleToUserForm
import com.project.lunchBooking.service.RoleService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/role")
class RoleController(
    private val roleService: RoleService
) {

    @PostMapping("/save")
    fun saveRole(@RequestBody role: Role): ResponseEntity<Role> {
        return ResponseEntity(roleService.saveRole(role), HttpStatus.CREATED)

    }

    @PostMapping("/addRoleToUser")
    fun saveRole(@RequestBody form: RoleToUserForm): ResponseEntity<SuccessResponse> {
        roleService.addRoleToUser(form.username, form.roleName)
        return ResponseEntity.ok()
            .body(SuccessResponse(message = "role '${form.roleName}' has been assigned to user ${form.username}"))

    }
}