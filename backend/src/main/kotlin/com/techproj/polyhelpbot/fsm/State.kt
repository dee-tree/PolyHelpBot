package com.techproj.polyhelpbot.fsm

import dev.inmo.micro_utils.fsm.common.State
import dev.inmo.tgbotapi.types.ChatId

@JvmInline
value class StateId(val value: Long)

private fun Int.toStateId() = StateId(this.toLong())

sealed interface ChatState : State {

    var silentEnter: Boolean
    var enterText: String?

    val stateId: StateId
        get() {
            return when (this) {
                is StopState -> 0.toStateId()
                is ExpectRootCommandOrTextState -> 1.toStateId()
                is LocationsState -> 2.toStateId()
            }
        }


    companion object {
        fun make(stateId: StateId, chatId: ChatId, silentEnter: Boolean = false, enterText: String? = null) = when (stateId.value) {
            0L -> StopState(chatId, silentEnter, enterText)
            1L -> ExpectRootCommandOrTextState(chatId, silentEnter, enterText)
            2L -> LocationsState(chatId, silentEnter, enterText)
            else -> throw IllegalStateException("Can't deserialize ChatState with state id ${stateId}!")
        }
    }

    override val context: ChatId

    fun toExpectRootCommandState(silentEnter: Boolean = false, enterText: String? = null) = ExpectRootCommandOrTextState(context, silentEnter, enterText)
    fun toWhereState(silentEnter: Boolean = false, enterText: String? = null) = LocationsState(context, silentEnter, enterText)
    fun toStopState(silentEnter: Boolean = false, enterText: String? = null) = StopState(context, silentEnter, enterText)
}


data class StopState(
    override val context: ChatId,
    override var silentEnter: Boolean = false,
    override var enterText: String? = null
) : ChatState {
}
