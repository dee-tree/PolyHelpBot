package com.techproj.polyhelpbot

import dev.inmo.micro_utils.fsm.common.State

@JvmInline
value class StateId(val value: Int)

typealias ChatId = Long

fun Int.toStateId() = StateId(this)

sealed interface ChatState : State {
    var silentEnter: Boolean
    var enterText: String?

    override val context: ChatId

    public val stateId: StateId
        get() {
            return when (this) {
                is StopState -> 0.toStateId()
                is ExpectRootCommandOrAnswerState -> 1.toStateId()
                is LocationsState -> 2.toStateId()
            }
        }


    companion object {
        fun make(stateId: StateId, chatId: ChatId, silentEnter: Boolean = false, enterText: String? = null) =
            when (stateId.value) {
                0 -> StopState(chatId, silentEnter, enterText)
                1 -> ExpectRootCommandOrAnswerState(chatId, silentEnter, enterText)
                2 -> LocationsState(chatId, silentEnter, enterText)
                else -> throw IllegalStateException("No ChatState with state id ${stateId}!")
            }
    }

    fun toExpectRootCommandOrAnswerState(silentEnter: Boolean = false, enterText: String? = null) =
        ExpectRootCommandOrAnswerState(context, silentEnter, enterText)

    fun toWhereState(silentEnter: Boolean = false, enterText: String? = null) =
        LocationsState(context, silentEnter, enterText)

    fun toStopState(silentEnter: Boolean = false, enterText: String? = null) =
        StopState(context, silentEnter, enterText)
}

data class ExpectRootCommandOrAnswerState(
    override val context: ChatId,
    override var silentEnter: Boolean = false,
    override var enterText: String? = null
) : ChatState {
    companion object

}

data class LocationsState(
    override val context: ChatId,
    override var silentEnter: Boolean = false,
    override var enterText: String? = null
) : ChatState {
    companion object

}

data class StopState(
    override val context: ChatId,
    override var silentEnter: Boolean = false,
    override var enterText: String? = null
) : ChatState {
    companion object
}
