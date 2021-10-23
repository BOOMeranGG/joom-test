package com.example.joomtest.data.dto

import java.time.LocalDateTime

data class JwtHolder(
    var token: String,
    var expired: LocalDateTime
)
