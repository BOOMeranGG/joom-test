package com.example.joomtest.data.dto.request

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserRequest(
    @get:Email
    val email: String,
    @get:NotBlank
    val password: String
)
