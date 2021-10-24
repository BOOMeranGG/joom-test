package com.example.joomtest.repository

import com.example.joomtest.data.dto.response.MeetingResponse
import com.example.joomtest.jooq.calendar.Tables.ACTION
import com.example.joomtest.jooq.calendar.Tables.MEETING
import com.example.joomtest.jooq.calendar.tables.pojos.Meeting
import com.example.joomtest.jooq.calendar.tables.records.MeetingRecord
import com.example.joomtest.mapper.ActionMeetingMapper
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
class MeetingRepository(
    private val dslContext: DSLContext
) {

    fun save(record: MeetingRecord): UUID {
        return dslContext.insertInto(MEETING)
            .set(record)
            .returning()
            .fetchOne()!!
            .guid
    }

    fun getById(guid: UUID): Meeting {
        return dslContext.select()
            .from(MEETING)
            .where(MEETING.GUID.eq(guid))
            .fetchOneInto(Meeting::class.java)
            ?: throw RuntimeException("Meeting $guid not found")
    }

    fun getMeetingsBetweenTimesFromCalendar(
        calendarId: Int,
        timeFrom: OffsetDateTime,
        timeTo: OffsetDateTime
    ): List<MeetingResponse> {
        return dslContext.select(MEETING.GUID, MEETING.DATE_TIME)
            .from(MEETING) // В ACTION стоит UNIQUE CONSTRAINT (action_id, calendar_id)
            .join(ACTION).on(ACTION.ACTION_ID.eq(MEETING.GUID))
            .where(ACTION.CALENDAR_ID.eq(calendarId))
            .and(MEETING.DATE_TIME.greaterThan(timeFrom))
            .and(MEETING.DATE_TIME.lessThan(timeTo))
            .fetch {
                ActionMeetingMapper.mapRecordToMeetingResponse(it)
            }
    }

    fun update(meeting: Meeting) {
        dslContext
            .newRecord(MEETING, meeting)
            .update()
    }
}