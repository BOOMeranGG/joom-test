package com.example.joomtest.data.dto.response

import java.time.OffsetDateTime
import java.util.UUID

data class MeetingResponse(
    val meetingGuid: UUID,
    val dateTimeFrom: OffsetDateTime,
    val dateTimeTo: OffsetDateTime
)
