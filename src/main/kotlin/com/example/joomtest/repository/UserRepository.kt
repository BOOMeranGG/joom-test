package com.example.joomtest.repository

import com.example.joomtest.jooq.calendar.Tables.CALENDAR_
import com.example.joomtest.jooq.calendar.Tables.USER
import com.example.joomtest.jooq.calendar.tables.pojos.User
import com.example.joomtest.jooq.calendar.tables.records.UserRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val dslContext: DSLContext
) {

    fun save(record: UserRecord): Int {
        return dslContext.insertInto(USER)
            .set(record)
            .returning()
            .fetchOne()!!
            .id
    }

    fun findByEmail(email: String): User? =
        dslContext.select()
            .from(USER)
            .where(USER.EMAIL.eq(email.lowercase()))
            .fetchOneInto(User::class.java)

    fun findById(id: Int): User? =
        dslContext.select()
            .from(USER)
            .where(USER.ID.eq(id))
            .fetchOneInto(User::class.java)

    fun getCalendarIdToEmailPairs(calendarId: List<Int>): List<Pair<Int, String>> {
        return dslContext.select(CALENDAR_.ID, USER.EMAIL)
            .from(USER)
            .join(CALENDAR_).on(CALENDAR_.USER_ID.eq(USER.ID))
            .where(CALENDAR_.ID.`in`(calendarId))
            .fetch { record ->
                record[CALENDAR_.ID] to record[USER.EMAIL]
            }.toList()
    }
}