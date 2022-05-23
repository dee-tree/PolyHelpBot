package com.techproj.polyhelpbot.states

import com.techproj.polyhelpbot.ChatId
import com.techproj.polyhelpbot.ChatState
import com.techproj.polyhelpbot.toStateId
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class StateDAO(id: EntityID<ChatId>) : Entity<ChatId>(id) {
    companion object : EntityClass<ChatId, StateDAO>(UserStatesTable) {
        fun ChatState.newDAO() = StateDAO.new {
            this.stateId = this@newDAO.stateId.value
            this.chatId = this@newDAO.context
        }
    }

    var chatId by UserStatesTable.chatId
    var stateId by UserStatesTable.stateId


    fun toModel(): ChatState? {
        return stateId?.let { ChatState.make(it.toStateId(), chatId) }
    }
}
