package com.techproj.polyhelpbot.fsm

import com.techproj.polyhelpbot.ChatId
import com.techproj.polyhelpbot.NewChatState
import com.techproj.polyhelpbot.fsm.chat.StateRepository
import dev.inmo.micro_utils.fsm.common.managers.DefaultStatesManagerRepo

class StatesManagerRepo(
    private val remoteRepo: StateRepository
) : DefaultStatesManagerRepo<NewChatState> {
    override suspend fun set(state: NewChatState) {
        remoteRepo.saveState(state)
    }

    override suspend fun removeState(state: NewChatState) {
        remoteRepo.removeState(state)
    }

    override suspend fun getStates(): List<NewChatState> {
        return remoteRepo.getAllStates()
    }

    override suspend fun getContextState(context: Any): NewChatState? {
        val id = when (context) {
            is ChatId -> context
            is dev.inmo.tgbotapi.types.ChatId -> context.chatId
            else -> 0
        }
        println("get state successfully: ${remoteRepo.getState(id)}")
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