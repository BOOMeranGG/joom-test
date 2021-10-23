package com.example.joomtest.controller

import com.example.joomtest.data.dto.UserInfo
import com.example.joomtest.data.dto.request.MeetingCreateRequest
import com.example.joomtest.data.dto.request.MeetingDetailsRequest
import com.example.joomtest.data.dto.response.MeetingResponse
import com.example.joomtest.service.MeetingService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid

@Api(tags = ["Встречи"])
@RestController
@RequestMapping("/meeting")
class MeetingController(
    private val meetingService: MeetingService
) {

    @ApiOperation(
        value = "Создание встречи"
    )
    @PostMapping
    fun createMeeting(
        @RequestBody @Valid meetingRequest: MeetingCreateRequest
    ): ResponseEntity<UUID> {
        val userInfo = SecurityContextHolder.getContext().authentication.principal as UserInfo
        val response = meetingService.createMeeting(meetingRequest, userInfo)

        return ResponseEntity.ok(response)
    }

    @ApiOperation(
        value = "Обновление деталей встречи"
    )
    @PutMapping
    fun updateMeetingDetails(@RequestBody meetingDetails: MeetingDetailsRequest) {
        val userInfo = SecurityContextHolder.getContext().authentication.principal as UserInfo

        meetingService.updateMeetingDetails(meetingDetails, userInfo)
    }

    @ApiOperation(
        value = "Получение информации о встрече"
    )
    @GetMapping
    fun getMeetingInfo(@RequestParam("meeting_guid") meetingGuid: UUID): ResponseEntity<MeetingResponse> {
        val userInfo = SecurityContextHolder.getContext().authentication.principal as UserInfo

        return ResponseEntity.ok(meetingService.getMeetingInfo(meetingGuid, userInfo))
    }
}