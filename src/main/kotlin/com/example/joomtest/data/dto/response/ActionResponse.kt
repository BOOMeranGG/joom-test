package com.example.joomtest.data.dto.response

import java.time.OffsetDateTime

data class ActionResponse(
    val actionId: Int,
    val isConfirmed: Boolean,
    val date: OffsetDateTime,
    val actionTypeId: Int,
    val actionTypeName: String
)
