package com.example.joomtest.data.dto.response

data class MeetingResponse(
    val description: String?,
    val videoConferenceLink: String?,
    val participantUsersInfo: List<UserMeetingInfoResponse>
)

data class UserMeetingInfoResponse(
    val email: String,
    val calendarId: Int,
    val isConfirmed: Boolean
)
