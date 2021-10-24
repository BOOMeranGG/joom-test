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
            date = record[MEETING.DATE_TIME],
            actionTypeId = record[ACTION.TYPE_ID],
            actionTypeName = actionTypeService.getNameById(record[ACTION.TYPE_ID])
        )
    }

    fun mapRecordToMeetingResponse(record: Record): MeetingResponse {
        return MeetingResponse(
            meetingGuid = record[MEETING.GUID],
            date = record[MEETING.DATE_TIME]
        )
    }
}