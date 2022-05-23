package com.techproj.polyhelpbot.fsm.chat

import com.techproj.polyhelpbot.*

interface StateRepository {
    suspend fun getAllStates(): List<NewChatState>
    suspend fun saveState(state: NewChatState)
    suspend fun removeState(state: NewChatState)
    suspend fun getState(chatId: ChatId): NewChatState?

    //    suspend fun getInitialExternalState(): ExternalState
    suspend fun getInitialExternalState(chatId: ChatId): ExternalChatState

    //    suspend fun getExternalState(stateId: StateId): ExternalState
    suspend fun getExternalState(chatId: ChatId, stateId: StateId): ExternalChatState

}

suspend fun NewChatState.save(repo: StateRepository) = repo.saveState(this)

suspend fun ExternalChatState.nextExternalStateViaVariant(
    repo: StateRepository,
    selectedVariant: StateConnection
): ExternalChatState {
    require(selectedVariant in variants)

    return repo.getExternalState(context, selectedVariant.stateId)
}

suspend fun ExternalChatState.nextExternalStateViaVariant(
    repo: StateRepository,
    selectedVariant: String
): ExternalChatState? {
    return variants
        .find { it.variant == selectedVariant }
        ?.let { return repo.getExternalState(context, it.stateId) }
}

