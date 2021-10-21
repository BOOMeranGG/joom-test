package com.example.joomtest.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

fun Date.convertToLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this.time)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

