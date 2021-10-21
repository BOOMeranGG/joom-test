package com.example.joomtest.security

import org.springframework.security.core.AuthenticationException

class JwtAuthenticationException(msg: String?) : AuthenticationException(msg)