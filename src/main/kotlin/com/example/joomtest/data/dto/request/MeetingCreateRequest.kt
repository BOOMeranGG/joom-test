package com.example.joomtest.data.dto.request

import java.time.OffsetDateTime
import javax.validation.constraints.NotEmpty

data class MeetingCreateRequest(
    val dateTimeFrom: OffsetDateTime,
    val dateTimeTo: OffsetDateTime,
    @get:NotEmpty val participantCalendarIds: List<Int>,
    val description: String,
    val videoConferenceLink: String
)
