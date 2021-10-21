package com.example.joomtest.data.dto.request

import java.util.UUID

data class MeetingDetailsRequest(
    val meetingGuid: UUID,
    val description: String,
    val videoConferenceLink: String
)
