package com.techproj.polyhelpbot.fsm

import com.techproj.polyhelpbot.db.state.StateRepository
import dev.inmo.micro_utils.fsm.common.managers.DefaultStatesManagerRepo
import kotlinx.coroutines.runBlocking

class StatesManagerRepo(
    private val map: MutableMap<Any, ChatState> = mutableMapOf(),
    private val remoteRepo: StateRepository
) : DefaultStatesManagerRepo<ChatState> {

    init {
        runBlocking {
            map.putAll(remoteRepo.getAllStates().map { it.context to it })
        }
    }

    override suspend fun set(state: ChatState) {
        map[state.context] = state
        remoteRepo.saveState(state)
    }

    override suspend fun removeState(state: ChatState) {
        map.remove(state.context)
        remoteRepo.removeState(state)
    }

    override suspend fun getStates(): List<ChatState> {
        return map.values.toList()
    }

    override suspend fun getContextState(context: Any): ChatState? {
        return map[context]
    }

    override suspend fun contains(context: Any): Boolean {
        return context in map
    }

}