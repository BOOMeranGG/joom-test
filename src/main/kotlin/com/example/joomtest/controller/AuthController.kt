package com.example.joomtest.controller

import com.example.joomtest.data.dto.JwtHolder
import com.example.joomtest.data.dto.request.UserRequest
import com.example.joomtest.service.UserService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Api(tags = ["Аутентификация"])
@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService
) {

    @ApiOperation(
        value = "Создание нового аккаунта"
    )
    @PostMapping("/register")
    fun registerUser(@RequestBody @Valid user: UserRequest): ResponseEntity<Any> {
        userService.register(user)
        return ResponseEntity(HttpStatus.OK)
    }

    @ApiOperation(
        value = "Получение токена авторизации"
    )
    @PostMapping("/login")
    fun login(@RequestBody @Valid user: UserRequest): ResponseEntity<JwtHolder> {
        return ResponseEntity.ok(userService.login(user))
    }
}