package com.example.joomtest.data.dto.response

data class MeetingInfoResponse(
    val description: String?,
    val videoConferenceLink: String?,
    val participantUsersInfo: List<UserMeetingInfoResponse>,
    val title: String?
)

data class UserMeetingInfoResponse(
    val email: String,
    val calendarId: Int,
    val isConfirmed: Boolean
)
