package com.example.joomtest.service

import com.example.joomtest.data.dto.UserInfo
import com.example.joomtest.data.dto.request.MeetingCreateRequest
import com.example.joomtest.data.dto.request.MeetingDetailsRequest
import com.example.joomtest.data.dto.request.MeetingParticipantsRequest
import com.example.joomtest.data.dto.response.MeetingInfoResponse
import com.example.joomtest.data.dto.response.MeetingResponse
import com.example.joomtest.data.dto.response.UserMeetingInfoResponse
import com.example.joomtest.data.enum.ActionType
import com.example.joomtest.data.enum.ServerError
import com.example.joomtest.exception.ServerException
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
    private val dateTimeIntervalService: DateTimeIntervalService
) {

    @Transactional
    fun createMeeting(meetingRequest: MeetingCreateRequest, userInfo: UserInfo): UUID {
        validateMeetingCreateRequest(meetingRequest)
        val meetingId = meetingRepository.save(MeetingRecord().also {
            it.userCreatorId = userInfo.userId
            it.description = meetingRequest.description
            it.videoConferenceLink = meetingRequest.videoConferenceLink
            it.dateTimeFrom = meetingRequest.dateTimeFrom
            it.dateTimeTo = meetingRequest.dateTimeTo
            it.isPrivate = meetingRequest.isPrivate
            it.title = meetingRequest.title
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

    @Transactional
    fun updateMeetingParticipants(meetingDetailsRequest: MeetingDetailsRequest, userInfo: UserInfo) {
        val meeting = meetingRepository.getById(meetingDetailsRequest.meetingGuid)
            ?: throw ServerException(ServerError.NOT_FOUND, "Meeting ${meetingDetailsRequest.meetingGuid} not found")
        if (meeting.userCreatorId != userInfo.userId) {
            throw ServerException(ServerError.BAD_REQUEST, "Access denied")
        }

        meeting.description = meetingDetailsRequest.description
        meeting.videoConferenceLink = meetingDetailsRequest.videoConferenceLink
        meetingRepository.update(meeting)
    }

    @Transactional
    fun updateMeetingParticipants(meetingParticipantsRequest: MeetingParticipantsRequest, userInfo: UserInfo) {
        val meeting = meetingRepository.getById(meetingParticipantsRequest.meetingGuid)
            ?: throw ServerException(ServerError.NOT_FOUND, "Meeting ${meetingParticipantsRequest.meetingGuid} not found")
        if (meeting.userCreatorId != userInfo.userId) {
            throw ServerException(ServerError.BAD_REQUEST, "Access denied")
        }

        val requestParticipants = meetingParticipantsRequest.participantCalendarIds + userInfo.calendarId
        val actions = actionRepository.getByActionGuid(meeting.guid)
        val participantsCalendarIds = actions.map {
            it.calendarId
        }

        deleteParticipants(
            requestParticipants = requestParticipants,
            participantsCalendarIds = participantsCalendarIds,
            meetingGuid = meeting.guid
        )
        saveNewParticipants(
            requestParticipants = requestParticipants,
            participantsCalendarIds = participantsCalendarIds,
            typeId = actions.first().typeId,
            meetingGuid = meeting.guid
        )
    }

    fun getMeetingInfo(meetingGuid: UUID, userInfo: UserInfo): MeetingInfoResponse {
        val meeting = meetingRepository.getByIdNotPrivate(meetingGuid)
            ?: throw ServerException(ServerError.NOT_FOUND, "Not private meeting $meetingGuid not found")

        val participantActions = actionRepository.getByActionGuid(meetingGuid)
        val calendarIdToEmailPairs = userRepository.getCalendarIdToEmailPairs(participantActions.map { it.calendarId })

        return MeetingInfoResponse(
            description = meeting.description,
            videoConferenceLink = meeting.videoConferenceLink,
            title = meeting.title,
            participantUsersInfo = calendarIdToEmailPairs.map { pair ->
                UserMeetingInfoResponse(
                    calendarId = pair.first,
                    email = pair.second,
                    isConfirmed = participantActions.first {
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
            throw ServerException(ServerError.NOT_FOUND, "Calendar with id $calendarId not found")
        }

        val meetingsResponse = meetingRepository.getMeetingsBetweenTimesFromCalendar(
            calendarId = calendarId,
            timeFrom = timeFrom,
            timeTo = timeTo,
            isPrivate = true
        )
        return meetingsResponse
    }

    fun getAvailableIntervalForParticipants(
        participantsCalendarIds: List<Int>,
        timeInMinutes: Long,
        fromDateTime: OffsetDateTime?
    ): OffsetDateTime {
        val sortedByDateFromAndToActionsTime =
            meetingRepository.getMeetingsTimeOrderByDateFromAndTo(participantsCalendarIds)

        return dateTimeIntervalService.calculateAvailableIntervalForParticipantActions(
            sortedByDateFromAndToActionsTime = sortedByDateFromAndToActionsTime,
            timeInMinutes = timeInMinutes,
            fromDateTime = fromDateTime
        )
    }

    private fun saveNewParticipants(
        requestParticipants: List<Int>,
        participantsCalendarIds: List<Int>,
        typeId: Int,
        meetingGuid: UUID
    ) {
        val participantsToAdd = requestParticipants.filterNot {
            participantsCalendarIds.contains(it)
        }
        val newActions = participantsToAdd
            .map { participantCalendarId ->
                Action().also {
                    it.calendarId = participantCalendarId
                    it.typeId = typeId
                    it.actionId = meetingGuid
                }
            }
        actionRepository.saveAll(newActions)
    }

    private fun deleteParticipants(
        requestParticipants: List<Int>,
        participantsCalendarIds: List<Int>,
        meetingGuid: UUID
    ) {
        val participantsIdToDelete = participantsCalendarIds.filterNot {
            requestParticipants.contains(it)
        }

        participantsIdToDelete.forEach {
            actionRepository.deleteByActionIdAndCalendarId(meetingGuid, it)
        }
    }

    private fun validateMeetingCreateRequest(meetingRequest: MeetingCreateRequest) {
        if (meetingRequest.dateTimeFrom.isAfter(meetingRequest.dateTimeTo)) {
            throw ServerException(ServerError.BAD_REQUEST, "DateTimeFrom cannot be after dateTimeTo")
        }
        if (meetingRequest.dateTimeFrom.isBefore(OffsetDateTime.now())) {
            throw ServerException(ServerError.BAD_REQUEST, "DateTimeFrom cannot be in past")
        }
    }
}