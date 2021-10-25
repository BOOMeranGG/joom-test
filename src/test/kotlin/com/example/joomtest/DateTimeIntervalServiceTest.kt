package com.example.joomtest

import com.example.joomtest.data.dto.ActionDateTimeHolder
import com.example.joomtest.service.DateTimeIntervalService
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class DateTimeIntervalServiceTest {

    private val dateTimeIntervalService = DateTimeIntervalService()
    private val fromDateTime = OffsetDateTime.now()
    private val actions = listOf(
        ActionDateTimeHolder(
            OffsetDateTime.parse("2021-10-28T14:30:00+03:00"),
            OffsetDateTime.parse("2021-10-28T15:30:00+03:00"),
        ),
        ActionDateTimeHolder(
            OffsetDateTime.parse("2021-10-28T15:40:00+03:00"),
            OffsetDateTime.parse("2021-10-28T16:30:00+03:00"),
        ),
        ActionDateTimeHolder(
            OffsetDateTime.parse("2021-10-28T19:30:00+03:00"),
            OffsetDateTime.parse("2021-10-28T20:30:00+03:00"),
        ),
        ActionDateTimeHolder(
            OffsetDateTime.parse("2021-10-28T21:30:00+03:00"),
            OffsetDateTime.parse("2021-10-28T22:30:00+03:00"),
        )
    )

    @Test
    fun `If action array is empty, should return sent fromDateTime`() {
        val result = dateTimeIntervalService.calculateAvailableIntervalForParticipantActions(
            sortedByDateFromAndToActionsTime = emptyList(),
            timeInMinutes = 25,
            fromDateTime = fromDateTime
        )

        assertEquals(fromDateTime, result)
    }

    @Test
    fun `Find the answer in the middle of the list`() {
        val result = dateTimeIntervalService.calculateAvailableIntervalForParticipantActions(
            sortedByDateFromAndToActionsTime = actions,
            timeInMinutes = 25,
            fromDateTime = OffsetDateTime.parse("2021-10-28T14:20:00+03:00")
        )
        assertEquals(OffsetDateTime.parse("2021-10-28T16:30:00+03:00"), result)
    }

    @Test
    fun `Find the answer at the end of the list`() {
        val result = dateTimeIntervalService.calculateAvailableIntervalForParticipantActions(
            sortedByDateFromAndToActionsTime = actions,
            timeInMinutes = 225,
            fromDateTime = OffsetDateTime.parse("2021-10-28T14:20:00+03:00")
        )
        assertEquals(OffsetDateTime.parse("2021-10-28T22:30:00+03:00"), result)
    }

    @Test
    fun `Should work correctly at the intersection of dates`() {
        val actions = listOf(
            ActionDateTimeHolder(
                OffsetDateTime.parse("2021-10-28T14:30:00+03:00"),
                OffsetDateTime.parse("2021-10-28T15:30:00+03:00"),
            ),
            ActionDateTimeHolder(
                OffsetDateTime.parse("2021-10-28T15:40:00+03:00"),
                OffsetDateTime.parse("2021-10-28T16:30:00+03:00"),
            ),
            ActionDateTimeHolder(
                OffsetDateTime.parse("2021-10-28T19:30:00+03:00"),
                OffsetDateTime.parse("2021-10-28T20:30:00+03:00"),
            ),
            ActionDateTimeHolder(
                OffsetDateTime.parse("2021-10-28T20:40:00+03:00"),
                OffsetDateTime.parse("2021-10-28T21:30:00+03:00"),
            ),
            ActionDateTimeHolder(
                OffsetDateTime.parse("2021-10-28T21:40:00+03:00"),
                OffsetDateTime.parse("2021-10-28T23:00:00+03:00"),
            ),
            ActionDateTimeHolder(
                OffsetDateTime.parse("2021-10-28T23:00:00+03:00"),
                OffsetDateTime.parse("2021-10-28T23:30:00+03:00"),
            )
        )

        val result = dateTimeIntervalService.calculateAvailableIntervalForParticipantActions(
            sortedByDateFromAndToActionsTime = actions,
            timeInMinutes = 225,
            fromDateTime = actions.first().dateTimeFrom.minusMinutes(15)
        )
        assertEquals(OffsetDateTime.parse("2021-10-28T23:30:00+03:00"), result)
    }
}