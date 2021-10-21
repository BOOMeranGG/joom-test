package com.example.joomtest.service

import com.example.joomtest.data.dto.UserInfo
import com.example.joomtest.data.dto.request.MeetingCreateRequest
import com.example.joomtest.data.dto.request.MeetingDetailsRequest
import com.example.joomtest.data.dto.response.MeetingResponse
import com.example.joomtest.data.dto.response.UserMeetingInfoResponse
import com.example.joomtest.data.enum.ActionType
import com.example.joomtest.jooq.calendar.tables.pojos.Action
import com.example.joomtest.jooq.calendar.tables.records.MeetingRecord
import com.example.joomtest.repository.ActionRepository
import com.example.joomtest.repository.MeetingRepository
import com.example.joomtest.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class MeetingService(
    private val meetingRepository: MeetingRepository,
    private val actionRepository: ActionRepository,
    private val actionTypeService: ActionTypeService,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createMeeting(meetingRequest: MeetingCreateRequest, userInfo: UserInfo): UUID {
        val meetingId = meetingRepository.save(MeetingRecord().also {
            it.userCreatorId = userInfo.userId
            it.description = meetingRequest.description
            it.videoConferenceLink = meetingRequest.videoConferenceLink
            it.dateTime = meetingRequest.dateTime
        })
        val meetingActionTypeId = actionTypeService.getIdByName(ActionType.MEETING.value)

        val actions = (meetingRequest.participantCalendarIds + userInfo.calendarId)
            .map { participantCalendarId ->
                Action().also {
                    it.calendarId = participantCalendarId
                    it.typeId = meetingActionTypeId
                    it.actionId = meetingId
                }
            }
        actionRepository.saveAll(actions)

        return meetingId
    }

    fun updateMeetingDetails(meetingDetailsRequest: MeetingDetailsRequest, userInfo: UserInfo) {
        val meeting = meetingRepository.getById(meetingDetailsRequest.meetingGuid)
        if (meeting.userCreatorId != userInfo.userId) {
            throw RuntimeException("Access denied")
        }

        meeting.description = meetingDetailsRequest.description
        meeting.videoConferenceLink = meetingDetailsRequest.videoConferenceLink
        meetingRepository.update(meeting)
    }

    fun getMeetingInfo(meetingGuid: UUID, userInfo: UserInfo): MeetingResponse {
        val meeting = meetingRepository.getById(meetingGuid)
        //TODO: Добавить поддержку видимости встреч

        val currentActions = actionRepository.getByActionGuid(meetingGuid)
        val calendarIdToEmailPairs = userRepository.getCalendarIdToEmailPairs(currentActions.map { it.calendarId })

        return MeetingResponse(
            description = meeting.description,
            videoConferenceLink = meeting.videoConferenceLink,
            participantUsersInfo = calendarIdToEmailPairs.map { pair ->
                UserMeetingInfoResponse(
                    calendarId = pair.first,
                    email = pair.second,
                    isConfirmed = currentActions.first { //TODO: Как-то не очень, нужно переделать
                        it.calendarId == pair.first
                    }.isConfirmed
                )
            }
        )
    }
}