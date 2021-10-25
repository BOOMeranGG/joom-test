package com.example.joomtest.data.dto.request

import java.util.UUID
import javax.validation.constraints.NotEmpty

data class MeetingParticipantsRequest(
    val meetingGuid: UUID,
    @get:NotEmpty val participantCalendarIds: List<Int>,
)