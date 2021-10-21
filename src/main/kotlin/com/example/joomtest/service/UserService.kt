package com.example.joomtest.service

import com.example.joomtest.data.dto.JwtHolder
import com.example.joomtest.data.dto.request.UserRequest
import com.example.joomtest.jooq.calendar.tables.pojos.User
import com.example.joomtest.jooq.calendar.tables.records.UserRecord
import com.example.joomtest.repository.CalendarRepository
import com.example.joomtest.repository.UserRepository
import com.example.joomtest.security.JwtTokenProvider
import com.example.joomtest.util.LOGIN_FAILED_MESSAGE
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val calendarRepository: CalendarRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun register(newUser: UserRequest) {
        val userId = userRepository.save(
            UserRecord().also {
                it.email = newUser.email.lowercase()
                it.password = passwordEncoder.encode(newUser.password)
            }
        )

        calendarRepository.initUserCalendar(userId)
    }

    fun login(userRequest: UserRequest): JwtHolder {
        val userPojo = userRepository.findByEmail(userRequest.email)
            ?: throw UsernameNotFoundException(LOGIN_FAILED_MESSAGE)
        if (!passwordEncoder.matches(userRequest.password, userPojo.password)) {
            throw BadCredentialsException(LOGIN_FAILED_MESSAGE)
        }
        val calendarId = calendarRepository.findIdByUserId(userPojo.id)

        return jwtTokenProvider.createToken(userPojo, calendarId)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun findById(id: Int): User? {
        return userRepository.findById(id)
    }
}