package com.example.joomtest.service

import com.example.joomtest.repository.ActionTypeRepository
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class ActionTypeService(
    private val actionTypeRepository: ActionTypeRepository
) {

    fun getIdByName(name: String): Int {
        return actionTypeRepository.getAll()
            .firstOrNull { it.name == name }?.id
            ?: throw RuntimeException("ACTION_TYPE with name = $name not found")
    }

    fun getNameById(id: Int): String {
        return actionTypeRepository.getAll()
            .firstOrNull { it.id == id }?.name
            ?: throw RuntimeException("ACTION_TYPE with id = $id not found")
    }
}