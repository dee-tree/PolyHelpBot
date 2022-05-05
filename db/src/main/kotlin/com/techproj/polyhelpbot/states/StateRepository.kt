package com.techproj.polyhelpbot.states

import com.techproj.polyhelpbot.ChatId
import com.techproj.polyhelpbot.ChatState

interface StateRepository {
    suspend fun getAllStates(): List<ChatState>
    suspend fun saveState(state: ChatState)
    suspend fun removeState(state: ChatState)
    suspend fun getState(chatId: ChatId): ChatState?

}

suspend fun ChatState.save(repo: StateRepository) = repo.saveState(this)