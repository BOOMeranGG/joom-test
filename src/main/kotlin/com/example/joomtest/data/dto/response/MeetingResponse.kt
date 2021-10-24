package com.example.joomtest.data.dto.response

import java.time.OffsetDateTime
import java.util.UUID

data class MeetingResponse(
    val meetingGuid: UUID,
    val date: OffsetDateTime
)
