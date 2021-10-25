package com.example.joomtest.repository

import com.example.joomtest.data.dto.response.ActionResponse
import com.example.joomtest.jooq.calendar.Tables.ACTION
import com.example.joomtest.jooq.calendar.Tables.MEETING
import com.example.joomtest.jooq.calendar.tables.pojos.Action
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
class ActionRepository(
    private val dslContext: DSLContext
) {

    fun saveAll(actions: List<Action>) {
        dslContext.batchInsert(
            actions.map {
                dslContext.newRecord(ACTION, it)
            }
        ).execute()
    }

    fun getByActionGuid(actionGuid: UUID): List<Action> {
        return dslContext.select()
            .from(ACTION)
            .where(ACTION.ACTION_ID.eq(actionGuid))
            .fetchInto(Action::class.java)
    }

    fun getNotConfirmed(calendarId: Int): List<Record> {
        return dslContext.select()
            .from(ACTION)
            .join(MEETING).on(MEETING.GUID.eq(ACTION.ACTION_ID))
            .where(ACTION.IS_CONFIRMED.isFalse)
            .and(ACTION.CALENDAR_ID.eq(calendarId))
            .fetch()
    }

    fun getConfirmed(calendarId: Int, dateStartTo: OffsetDateTime): List<Record> {
        return dslContext.select()
            .from(ACTION)
            .join(MEETING).on(MEETING.GUID.eq(ACTION.ACTION_ID))
            .where(ACTION.IS_CONFIRMED.isTrue)
            .and(ACTION.CALENDAR_ID.eq(calendarId))
            .and(MEETING.DATE_TIME_FROM.lessThan(dateStartTo))
            .fetch()
    }

    fun setConfirmAction(actionId: Int, calendarId: Int, isConfirmed: Boolean): Boolean {
        val result = dslContext.update(ACTION)
            .set(ACTION.IS_CONFIRMED, isConfirmed)
            .where(ACTION.IS_CONFIRMED.notEqual(isConfirmed))
            .and(ACTION.ID.eq(actionId))
            .and(ACTION.CALENDAR_ID.eq(calendarId))
            .returningResult(ACTION.ID)
            .fetchOne()

        return result != null
    }

    fun deleteByActionIdAndCalendarId(actionId: UUID, calendarId: Int) {
        dslContext.deleteFrom(ACTION)
            .where(ACTION.ACTION_ID.eq(actionId))
            .and(ACTION.CALENDAR_ID.eq(calendarId))
            .execute()
    }
}