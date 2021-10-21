package com.example.joomtest.security

import com.example.joomtest.data.dto.JwtHolder
import com.example.joomtest.data.dto.UserInfo
import com.example.joomtest.jooq.calendar.tables.pojos.User
import com.example.joomtest.util.convertToLocalDateTime
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.Date
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider(
    @Value("\${jwt.token.secret}")
    private val jwtSecret: String,

    @Value("\${jwt.token.expired}")
    private val validityInMilliseconds: Long
) {

    private val calendarIdClaimsName = "calendar_id"
    private val userIdClaimsName = "user_id"

    fun createToken(user: User, calendarId: Int): JwtHolder {
        val claims = Jwts.claims().setSubject(user.email)
        claims[calendarIdClaimsName] = calendarId
        claims[userIdClaimsName] = user.id

        val nowDate = Date()
        val validityDate = Date(nowDate.time + validityInMilliseconds)

        val token = Jwts.builder()
            .signWith(SignatureAlgorithm.HS256, jwtSecret)
            .setClaims(claims)
            .setIssuedAt(nowDate)
            .setExpiration(validityDate)
            .compact()

        return JwtHolder(
            token = token,
            expired = validityDate.convertToLocalDateTime().toString()
        )
    }

    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)

        return UsernamePasswordAuthenticationToken(
            UserInfo(
                calendarId = claims.body[calendarIdClaimsName] as Int,
                userId = claims.body[userIdClaimsName] as Int,
                email = getUserEmail(token)
            ),
            "",
            emptyList()
        )
    }

    fun getUserEmail(token: String): String {
        return Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .body
            .subject
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length)
        }

        return null
    }

    fun validateToken(token: String): Boolean {
        try {
            val claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)

            return claims.body.expiration.after(Date())
        } catch (e: JwtException) {
            throw JwtAuthenticationException("JWT token is expired or invalid")
        } catch (e: IllegalArgumentException) {
            throw JwtAuthenticationException("JWT token is expired or invalid")
        }
    }

    private fun getClaims(token: String): Jws<Claims> {
        try {
            return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token)
        } catch (e: JwtException) { // TODO: в ответ эта информация не попадает!
            throw JwtAuthenticationException("JWT token is expired or invalid")
        } catch (e: IllegalArgumentException) {
            throw JwtAuthenticationException("JWT token is expired or invalid")
        }
    }
}