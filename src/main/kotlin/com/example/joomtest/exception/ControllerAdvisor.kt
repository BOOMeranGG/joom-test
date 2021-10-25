package com.example.joomtest.exception

import com.example.joomtest.data.dto.ExceptionDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerAdvisor(
    private val objectMapper: ObjectMapper
) {

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationExceptions(ex: AuthenticationException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(ServerException::class)
    fun handleServerException(ex: ServerException): ResponseEntity<String> {
        val exceptionDto = ExceptionDto(
            description = ex.description
        )
        return ResponseEntity(objectMapper.writeValueAsString(exceptionDto), ex.error.status)
    }
}