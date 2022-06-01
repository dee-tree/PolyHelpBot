package com.techproj.polyhelpbot

import dev.inmo.micro_utils.fsm.common.State
import kotlinx.serialization.SerialName
import kotlin.reflect.KClass

@JvmInline
@kotlinx.serialization.Serializable
value class StateId(val value: Int)


typealias ChatId = Long

fun Int.toStateId() = StateId(this)


private val idsToNewStatesMatching = mapOf<StateId, KClass<out InternalChatState>>(
    (-1).toStateId() to StopChatState::class,
)

private val newStatesToIdsMatching = idsToNewStatesMatching.entries.associate { (k, v) -> v to k }


sealed interface NewChatState : State {
    override val context: ChatId
    val stateId: StateId

    var silentEnter: Boolean
    var enterText: String?
}

sealed class InternalChatState(
    override val context: ChatId
) : NewChatState {

    override val stateId: StateId
        get() = newStatesToIdsMatching[this::class]!!

    companion object {

        fun make(stateId: StateId, chatId: ChatId, silentEnter: Boolean = false, enterText: String? = null) =
            idsToNewStatesMatching[stateId]!!.constructors.first().call(chatId, silentEnter, enterText)
    }
}

data class StopChatState(
    override val context: ChatId,
    override var silentEnter: Boolean = false,
    override var enterText: String? = null
) : InternalChatState(context) {
}


data class ExternalChatState(
    override val context: ChatId,
    override val stateId: StateId,
    val variants: List<StateConnection>,
    val description: String? = null,
    override var silentEnter: Boolean = false,
    override var enterText: String? = null
) : NewChatState {
    companion object

}

@kotlinx.serialization.Serializable
data class StateConnection(
    @SerialName("variant")
    val variant: String,
    @SerialName("stateId")
    val stateId: StateId,
    @SerialName("answerId")
    val answerId: AnswerId? = null
)

fun ExternalChatState.findVariant(userAnswer: String): StateConnection? =
    variants.find { it.variant == userAnswer }


@JvmInline
@kotlinx.serialization.Serializable
value class AnswerId(val value: String) {

}
