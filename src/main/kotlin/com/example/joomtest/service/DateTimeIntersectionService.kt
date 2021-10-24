package com.example.joomtest.service

import com.example.joomtest.data.dto.ActionDateTimeHolder
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class DateTimeIntersectionService {

    fun calculateAvailableIntervalForParticipantActions(
        sortedByDateFromAndToActionsTime: List<ActionDateTimeHolder>,
        timeInMinutes: Long,
        fromDateTime: OffsetDateTime?
    ): OffsetDateTime {
        val mergedBlockedTimes = mergeIntersectionsBlockedTimes(sortedByDateFromAndToActionsTime.distinct())
        val startDate = fromDateTime ?: OffsetDateTime.now().plusMinutes(1)

        val minutesBeforeFirstAction = getMinutesBetweenOffsetDates(startDate, mergedBlockedTimes.first().dateTimeFrom)
        if (minutesBeforeFirstAction >= timeInMinutes) {
            return startDate
        }

        for (i in 0 until mergedBlockedTimes.size - 1) {
            val minutesToNextAction = getMinutesBetweenOffsetDates(
                mergedBlockedTimes[i].dateTimeTo,
                mergedBlockedTimes[i + 1].dateTimeFrom
            )

            if (minutesToNextAction >= timeInMinutes) {
                return mergedBlockedTimes[i].dateTimeTo
            }
        }

        return mergedBlockedTimes.last().dateTimeTo
    }

    private fun mergeIntersectionsBlockedTimes(sortedBlockedTimes: List<ActionDateTimeHolder>): List<ActionDateTimeHolder> {
        val resultBlockedTimes = mutableListOf<ActionDateTimeHolder>()

        var i = 0
        while (i < sortedBlockedTimes.size) {
            var currentTimeHolder = sortedBlockedTimes[i]

            while (i < sortedBlockedTimes.size - 1) {
                val nextTimeHolder = sortedBlockedTimes[i + 1]

                // Объединение пересекающихся дат
                if (currentTimeHolder.dateTimeTo.isAfter(nextTimeHolder.dateTimeFrom)) {
                    currentTimeHolder = currentTimeHolder.copy(
                        dateTimeTo = nextTimeHolder.dateTimeTo
                    )
                    i++
                } else break
            }
            i++

            resultBlockedTimes.add(currentTimeHolder)
        }

        return resultBlockedTimes
    }

    private fun getMinutesBetweenOffsetDates(
        first: OffsetDateTime,
        second: OffsetDateTime
    ): Long {
        return (second.toEpochSecond() - first.toEpochSecond()) / 60
    }
}