package com.example.joomtest.service

import com.example.joomtest.data.dto.UserInfo
import com.example.joomtest.data.dto.response.ActionResponse
import com.example.joomtest.data.enum.ServerError
import com.example.joomtest.exception.ServerException
import com.example.joomtest.mapper.ActionMeetingMapper
import com.example.joomtest.repository.ActionRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class ActionService(
    private val actionRepository: ActionRepository,
    private val actionTypeService: ActionTypeService
) {

    fun getNotConfirmedMeetingActions(userInfo: UserInfo): List<ActionResponse> {
        return actionRepository.getNotConfirmed(userInfo.calendarId).map {
            ActionMeetingMapper.mapRecordToActionResponse(it, actionTypeService)
        }
    }

    fun getConfirmedMeetingActions(dateStartTo: OffsetDateTime, userInfo: UserInfo): List<ActionResponse> {
        return actionRepository.getConfirmed(userInfo.calendarId, dateStartTo).map {
            ActionMeetingMapper.mapRecordToActionResponse(it, actionTypeService)
        }
    }

    fun confirmMeetingsAction(actionId: Int, isConfirmed: Boolean, userInfo: UserInfo) {
        val isUpdateSuccess = actionRepository.setConfirmAction(actionId, userInfo.calendarId, isConfirmed)

        if (!isUpdateSuccess) {
            throw ServerException(ServerError.NOT_FOUND, "Action not found")
        }
    }
}