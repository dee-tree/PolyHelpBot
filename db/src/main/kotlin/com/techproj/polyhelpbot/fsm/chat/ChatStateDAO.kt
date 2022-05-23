package com.techproj.polyhelpbot.fsm.chat

import com.techproj.polyhelpbot.*
import com.techproj.polyhelpbot.fsm.StateQuestionsDAO
import com.techproj.polyhelpbot.fsm.StatesQuestionsTable
import com.techproj.polyhelpbot.fsm.connections.StateConnectionsDAO
import com.techproj.polyhelpbot.fsm.connections.StatesConnectionsTable
import com.techproj.polyhelpbot.locations.PhysicalPlaceKeywordsTable.uniqueIndex
import com.techproj.polyhelpbot.states.StateDAO
import com.techproj.polyhelpbot.states.UserStatesTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

internal class ChatStateDAO(id: EntityID<ChatId>) : Entity<ChatId>(id) {

    var chatId by ChatStateTable.chatId
    var state by ChatStateTable.stateId

    companion object : EntityClass<ChatId, ChatStateDAO>(ChatStateTable) {
        fun NewChatState.newChatStateDAO() : ChatStateDAO {
//            return ChatStateDAO.new(this.context) {
            return ChatStateDAO.new {
                this.chatId = this@newChatStateDAO.context
                this.state = this@newChatStateDAO.stateId.value
            }
        }
    }

    fun toModel(): NewChatState? = transaction {
        return@transaction when {
            state == null -> null
            state!! < 0 -> InternalChatState.make(state!!.toStateId(), chatId)
            else -> ExternalChatState(chatId, state!!.toStateId(), StateConnectionsDAO.find { StatesConnectionsTable.initialStateId eq state }.map { it.toModel() }, description = StateQuestionsDAO.find { StatesQuestionsTable.id eq state }.first().description)
        }
    }
}