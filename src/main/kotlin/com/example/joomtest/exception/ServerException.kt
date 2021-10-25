package com.example.joomtest.exception

import com.example.joomtest.data.enum.ServerError

class ServerException(
    val error: ServerError,
    val description: String
) : RuntimeException(description)