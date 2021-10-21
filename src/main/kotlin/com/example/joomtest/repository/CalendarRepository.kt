package com.example.joomtest.repository

import com.example.joomtest.jooq.calendar.Tables.CALENDAR_
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class CalendarRepository(
    private val dslContext: DSLContext
) {

    private val calendar = CALENDAR_

    fun initUserCalendar(userId: Int): Int {
        return dslContext.insertInto(calendar)
            .set(calendar.USER_ID, userId)
            .returning()
            .fetchOne()!!
            .id
    }

    fun findIdByUserId(userId: Int): Int {
        return dslContext.select()
            .from(calendar)
            .where(calendar.USER_ID.eq(userId))
            .fetchOne { record -> record[calendar.ID] }
            ?: throw RuntimeException("")
    }
}