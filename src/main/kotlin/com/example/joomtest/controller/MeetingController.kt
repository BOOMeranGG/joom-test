package com.example.joomtest.controller

import com.example.joomtest.data.dto.UserInfo
import com.example.joomtest.data.dto.request.MeetingCreateRequest
import com.example.joomtest.data.dto.request.MeetingDetailsRequest
import com.example.joomtest.data.dto.request.MeetingParticipantsRequest
import com.example.joomtest.data.dto.response.MeetingInfoResponse
import com.example.joomtest.data.dto.response.MeetingResponse
import com.example.joomtest.service.MeetingService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.Min

@Api(tags = ["Встречи"])
@Validated
@RestController
class MeetingController(
    private val meetingService: MeetingService
) {

    @ApiOperation(
        value = "Создание встречи"
    )
    @PostMapping("/meeting")
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
    @PutMapping("/meeting")
    fun updateMeetingDetails(@RequestBody meetingDetails: MeetingDetailsRequest) {
        val userInfo = SecurityContextHolder.getContext().authentication.principal as UserInfo

        meetingService.updateMeetingParticipants(meetingDetails, userInfo)
    }

    @ApiOperation(
        value = "Обновление участников встречи"
    )
    @PutMapping("/meeting/participants")
    fun updateMeetingParticipants(@RequestBody request: MeetingParticipantsRequest) {
        val userInfo = SecurityContextHolder.getContext().authentication.principal as UserInfo

        meetingService.updateMeetingParticipants(request, userInfo)
    }

    @ApiOperation(
        value = "Получение информации о встрече"
    )
    @GetMapping("/meeting")
    fun getMeetingInfo(@RequestParam("meeting_guid") meetingGuid: UUID): ResponseEntity<MeetingInfoResponse> {
        val userInfo = SecurityContextHolder.getContext().authentication.principal as UserInfo
        val response = meetingService.getMeetingInfo(meetingGuid, userInfo)

        return ResponseEntity.ok(response)
    }

    @ApiOperation(
        value = "Получение списка встреч указанного пользователя по заданному промежутку времени"
    )
    @GetMapping("/meetings")
    fun getMeetingsBetweenTimes(
        @RequestParam("calendar_id")
        @Min(1)
        calendarId: Int,

        @RequestParam("date_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        timeFrom: OffsetDateTime,

        @RequestParam("date_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        timeTo: OffsetDateTime
    ): ResponseEntity<List<MeetingResponse>> {
        val response = meetingService.getMeetingsBetweenTimes(calendarId, timeFrom, timeTo)

        return ResponseEntity.ok(response)
    }

    @ApiOperation(
        value = "Ближайший свободный интервал времени для пользователей"
    )
    @GetMapping("/time_interval/available")
    fun getAvailableIntervalForParticipants(
        @RequestParam("participants_calendar_ids") participantsCalendarIds: List<Int>,
        @RequestParam("time_in_minutes") timeInMinutes: Long,

        @RequestParam("from_date_time", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        fromDateTime: OffsetDateTime?
    ): ResponseEntity<OffsetDateTime> {
        val response = meetingService.getAvailableIntervalForParticipants(
            participantsCalendarIds = participantsCalendarIds,
            timeInMinutes = timeInMinutes,
            fromDateTime = fromDateTime
        )

        return ResponseEntity.ok(response)
    }
}