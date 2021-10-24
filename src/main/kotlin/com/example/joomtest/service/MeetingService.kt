package com.example.joomtest.service

import com.example.joomtest.data.dto.UserInfo
import com.example.joomtest.data.dto.request.MeetingCreateRequest
import com.example.joomtest.data.dto.request.MeetingDetailsRequest
import com.example.joomtest.data.dto.response.MeetingInfoResponse
import com.example.joomtest.data.dto.response.MeetingResponse
import com.example.joomtest.data.dto.response.UserMeetingInfoResponse
import com.example.joomtest.data.enum.ActionType
import com.example.joomtest.jooq.calendar.tables.pojos.Action
import com.example.joomtest.jooq.calendar.tables.records.MeetingRecord
import com.example.joomtest.repository.ActionRepository
import com.example.joomtest.repository.CalendarRepository
import com.example.joomtest.repository.MeetingRepository
import com.example.joomtest.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class MeetingService(
    private val meetingRepository: MeetingRepository,
    private val actionRepository: ActionRepository,
    private val actionTypeService: ActionTypeService,
    private val calendarRepository: CalendarRepository,
    private val userRepository: UserRepository,
    private val dateTimeIntersectionService: DateTimeIntersectionService
) {

    @Transactional
    fun createMeeting(meetingRequest: MeetingCreateRequest, userInfo: UserInfo): UUID {
        val meetingId = meetingRepository.save(MeetingRecord().also {
            it.userCreatorId = userInfo.userId
            it.description = meetingRequest.description
            it.videoConferenceLink = meetingRequest.videoConferenceLink
            it.dateTimeFrom = meetingRequest.dateTimeFrom
            it.dateTimeTo = meetingRequest.dateTimeTo
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

    fun getMeetingInfo(meetingGuid: UUID, userInfo: UserInfo): MeetingInfoResponse {
        val meeting = meetingRepository.getById(meetingGuid)
        //TODO: Добавить поддержку видимости встреч

        val participantActions = actionRepository.getByActionGuid(meetingGuid)
        val calendarIdToEmailPairs = userRepository.getCalendarIdToEmailPairs(participantActions.map { it.calendarId })

        return MeetingInfoResponse(
            description = meeting.description,
            videoConferenceLink = meeting.videoConferenceLink,
            participantUsersInfo = calendarIdToEmailPairs.map { pair ->
                UserMeetingInfoResponse(
                    calendarId = pair.first,
                    email = pair.second,
                    isConfirmed = participantActions.first { //TODO: Как-то не очень, нужно переделать
                        it.calendarId == pair.first
                    }.isConfirmed
                )
            }
        )
    }

    fun getMeetingsBetweenTimes(
        calendarId: Int,
        timeFrom: OffsetDateTime,
        timeTo: OffsetDateTime
    ): List<MeetingResponse> {
        // TODO: Добавить поддержку видимости встреч
        val isCalendarExist = calendarRepository.isExistById(calendarId)
        if (!isCalendarExist) {
            throw RuntimeException("Calendar with id $calendarId not found")
        }

        val meetingsResponse = meetingRepository.getMeetingsBetweenTimesFromCalendar(calendarId, timeFrom, timeTo)
        return meetingsResponse
    }

    fun getAvailableIntervalForParticipants(
        participantsCalendarIds: List<Int>,
        timeInMinutes: Long,
        fromDateTime: OffsetDateTime?
    ): OffsetDateTime {
        val sortedByDateFromAndToActionsTime =
            meetingRepository.getMeetingsTimeOrderByDateFromAndTo(participantsCalendarIds)

        return dateTimeIntersectionService.calculateAvailableIntervalForParticipantActions(
            sortedByDateFromAndToActionsTime = sortedByDateFromAndToActionsTime,
            timeInMinutes = timeInMinutes,
            fromDateTime = fromDateTime
        )
    }
}