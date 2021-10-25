package com.example.joomtest.mapper

import com.example.joomtest.data.dto.response.ActionResponse
import com.example.joomtest.data.dto.response.MeetingResponse
import com.example.joomtest.jooq.calendar.Tables.ACTION
import com.example.joomtest.jooq.calendar.Tables.MEETING
import com.example.joomtest.service.ActionTypeService
import org.jooq.Record

object ActionMeetingMapper {

    fun mapRecordToActionResponse(record: Record, actionTypeService: ActionTypeService): ActionResponse {
        return ActionResponse(
            actionId = record[ACTION.ID],
            isConfirmed = record[ACTION.IS_CONFIRMED],
            dateTimeFrom = record[MEETING.DATE_TIME_FROM],
            dateTimeTo = record[MEETING.DATE_TIME_TO],
            actionTypeId = record[ACTION.TYPE_ID],
            actionTypeName = actionTypeService.getNameById(record[ACTION.TYPE_ID])
        )
    }

    fun mapRecordToMeetingResponse(record: Record): MeetingResponse {
        return MeetingResponse(
            meetingGuid = record[MEETING.GUID],
            title = record[MEETING.TITLE],
            dateTimeFrom = record[MEETING.DATE_TIME_FROM],
            dateTimeTo = record[MEETING.DATE_TIME_TO]
        )
    }
}