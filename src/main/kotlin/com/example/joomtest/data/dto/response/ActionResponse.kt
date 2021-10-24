package com.example.joomtest.data.dto.response

import java.time.OffsetDateTime

data class ActionResponse(
    val actionId: Int,
    val isConfirmed: Boolean,
    val dateTimeFrom: OffsetDateTime,
    val dateTimeTo: OffsetDateTime,
    val actionTypeId: Int,
    val actionTypeName: String
)
