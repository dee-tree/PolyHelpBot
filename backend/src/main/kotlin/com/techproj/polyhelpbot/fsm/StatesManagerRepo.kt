package com.techproj.polyhelpbot.fsm

import com.techproj.polyhelpbot.ChatId
import com.techproj.polyhelpbot.ChatState
import com.techproj.polyhelpbot.states.StateRepository
import dev.inmo.micro_utils.fsm.common.managers.DefaultStatesManagerRepo

class StatesManagerRepo(
    private val remoteRepo: StateRepository
) : DefaultStatesManagerRepo<ChatState> {
    override suspend fun set(state: ChatState) {
        remoteRepo.saveState(state)
    }

    override suspend fun removeState(state: ChatState) {
        remoteRepo.removeState(state)
    }

    override suspend fun getStates(): List<ChatState> {
        return remoteRepo.getAllStates()
    }

    override suspend fun getContextState(context: Any): ChatState? {
        val id = when (context) {
            is ChatId -> context
            is dev.inmo.tgbotapi.types.ChatId -> context.chatId
            else -> 0
        }
        return remoteRepo.getState(id)
    }

    override suspend fun contains(context: Any): Boolean {
        val id = when (context) {
            is ChatId -> context
            is dev.inmo.tgbotapi.types.ChatId -> context.chatId
            else -> 0
        }

        return remoteRepo.getState(id) != null
    }

}