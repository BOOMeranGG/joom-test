package com.example.joomtest.repository

import com.example.joomtest.jooq.calendar.Tables.ACTION_TYPE
import com.example.joomtest.jooq.calendar.tables.pojos.ActionType
import org.jooq.DSLContext
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository

@Repository
@CacheConfig(cacheNames = ["action_types"])
class ActionTypeRepository(
    private val dslContext: DSLContext
) {

    @Cacheable
    fun getAll(): List<ActionType> {
        return dslContext.select()
            .from(ACTION_TYPE)
            .fetchInto(ActionType::class.java)
    }
}