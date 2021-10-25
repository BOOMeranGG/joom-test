package com.example.joomtest.data.enum

import org.springframework.http.HttpStatus

enum class ServerError(val status: HttpStatus) {
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_REQUEST(HttpStatus.UNAUTHORIZED),
    NOT_FOUND(HttpStatus.NOT_FOUND),
}