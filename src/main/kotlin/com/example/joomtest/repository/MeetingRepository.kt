package com.example.joomtest.repository

import com.example.joomtest.jooq.calendar.Tables.MEETING
import com.example.joomtest.jooq.calendar.tables.pojos.Meeting
import com.example.joomtest.jooq.calendar.tables.records.MeetingRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
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

    fun update(meeting: Meeting) {
        dslContext
            .newRecord(MEETING, meeting)
            .update()
    }
}