package com.example.joomtest.controller

import com.example.joomtest.data.dto.UserInfo
import com.example.joomtest.service.ActionService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime

@RestController
class ActionController(
    private val actionService: ActionService
) {

    @GetMapping("/actions/not_confirmed")
    fun getNotConfirmedActions(): ResponseEntity<Any> {
        val userInfo = SecurityContextHolder.getContext().authentication.principal as UserInfo

        return ResponseEntity.ok(actionService.getNotConfirmedMeetingActions(userInfo))
    }

    @GetMapping("/actions/confirmed")
    fun getConfirmedActions(
        @RequestParam("date_to")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        dateTo: OffsetDateTime
    ): ResponseEntity<Any> {
        val userInfo = SecurityContextHolder.getContext().authentication.principal as UserInfo

        return ResponseEntity.ok(actionService.getConfirmedMeetingActions(dateTo, userInfo))
    }

    @PutMapping("/action/confirm")
    fun setConfirmationAction(
        @RequestParam("action_id") actionId: Int,
        @RequestParam("is_confirmed") isConfirmed: Boolean
    ) {
        val userInfo = SecurityContextHolder.getContext().authentication.principal as UserInfo

        actionService.confirmMeetingsAction(actionId, isConfirmed, userInfo)
    }
}