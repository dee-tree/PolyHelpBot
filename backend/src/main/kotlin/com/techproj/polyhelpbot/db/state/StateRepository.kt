package com.techproj.polyhelpbot.db.state

import com.techproj.polyhelpbot.db.BasePath
import com.techproj.polyhelpbot.fsm.ChatState
import dev.inmo.tgbotapi.types.ChatId

interface StateRepository {
    suspend fun getAllStates(): List<ChatState>
    suspend fun saveState(state: ChatState)
    suspend fun removeState(state: ChatState)
    suspend fun getState(chatId: ChatId): ChatState?

    object Path {
        fun state(chatId: ChatId) = "${BasePath.chats}/${chatId.chatId}/state"
    }
}

suspend fun ChatState.save(repo: StateRepository) = repo.saveState(this)