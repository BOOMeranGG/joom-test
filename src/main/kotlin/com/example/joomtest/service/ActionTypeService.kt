package com.example.joomtest.service

import com.example.joomtest.data.enum.ServerError
import com.example.joomtest.exception.ServerException
import com.example.joomtest.repository.ActionTypeRepository
import org.springframework.stereotype.Service

@Service
class ActionTypeService(
    private val actionTypeRepository: ActionTypeRepository
) {

    fun getIdByName(name: String): Int {
        return actionTypeRepository.getAll()
            .firstOrNull { it.name == name }?.id
            ?: throw ServerException(ServerError.NOT_FOUND, "ACTION_TYPE with name = $name not found")
    }

    fun getNameById(id: Int): String {
        return actionTypeRepository.getAll()
            .firstOrNull { it.id == id }?.name
            ?: throw ServerException(ServerError.NOT_FOUND, "ACTION_TYPE with id = $id not found")
    }
}