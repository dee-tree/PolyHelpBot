package com.techproj.polyhelpbot

import dev.inmo.micro_utils.fsm.common.State
import kotlinx.serialization.SerialName
import kotlin.reflect.KClass

@JvmInline
@kotlinx.serialization.Serializable
value class StateId(val value: Int)

/*@JvmInline
@kotlinx.serialization.Serializable
value class ExternalStateId(val value: String) {

}*/

typealias ChatId = Long

fun Int.toStateId() = StateId(this)


@Deprecated("use new states")
private val idsToStatesMatching = mapOf<StateId, KClass<out ChatState>>(
    0.toStateId() to StopState::class,
    1.toStateId() to ExpectRootCommandOrAnswerState::class,
    2.toStateId() to LocationsState::class,
    3.toStateId() to PostAdmissionInformationState::class
)

@Deprecated("use new states")
private val statesToIdsMatching = idsToStatesMatching.entries.associate { (k, v) -> v to k }



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


/*@kotlinx.serialization.Serializable
data class ExternalState(
    @SerialName("id")
    val id: ExternalStateId,
    @SerialName("variants")
    val variants: List<StateConnection>,
    @SerialName("description")
    val description: String? = null
) {

    init {
        require(variants.isNotEmpty())
    }
}*/

@kotlinx.serialization.Serializable
data class StateConnection(
    @SerialName("variant")
    val variant: String,
    @SerialName("stateId")
    val stateId: StateId,
    @SerialName("answerId")
    val answerId: AnswerId? = null
)


@JvmInline
@kotlinx.serialization.Serializable
value class AnswerId(val value: String) {

}

//fun ExternalState.toChatExternalState(chatId: ChatId) = ExternalChatState(chatId, id, variants, description)

/////////////////



@Deprecated("use new states")
sealed interface ChatState : State {
    var silentEnter: Boolean
    var enterText: String?

    override val context: ChatId

    public val stateId: StateId
        get() {
            return statesToIdsMatching[this::class]!!
            /*return when (this) {
                is StopState -> 0.toStateId()
                is ExpectRootCommandOrAnswerState -> 1.toStateId()
                is LocationsState -> 2.toStateId()
                is PostAdmissionInformation ->
            }*/
        }


    companion object {

        fun make(stateId: StateId, chatId: ChatId, silentEnter: Boolean = false, enterText: String? = null) =
            idsToStatesMatching[stateId]!!.constructors.first().call(chatId, silentEnter, enterText)
//            when (stateId.value) {
//                0 -> StopState(chatId, silentEnter, enterText)
//                1 -> ExpectRootCommandOrAnswerState(chatId, silentEnter, enterText)
//                2 -> LocationsState(chatId, silentEnter, enterText)
//                else -> throw IllegalStateException("No ChatState with state id ${stateId}!")
//            }
    }

    fun toExpectRootCommandOrAnswerState(silentEnter: Boolean = false, enterText: String? = null) =
        ExpectRootCommandOrAnswerState(context, silentEnter, enterText)

    fun toWhereState(silentEnter: Boolean = false, enterText: String? = null) =
        LocationsState(context, silentEnter, enterText)

    fun toStopState(silentEnter: Boolean = false, enterText: String? = null) =
        StopState(context, silentEnter, enterText)
}

@Deprecated("use new states")
data class ExpectRootCommandOrAnswerState(
    override val context: ChatId,
    override var silentEnter: Boolean = false,
    override var enterText: String? = null
) : ChatState {
    companion object

    fun fromOtherState(state: ChatState, silentEnter: Boolean? = null, enterText: String? = null): ExpectRootCommandOrAnswerState = ExpectRootCommandOrAnswerState(
        state.context,
        silentEnter ?: state.silentEnter,
        enterText ?: state.enterText
    )

}

@Deprecated("use new states")
data class LocationsState(
    override val context: ChatId,
    override var silentEnter: Boolean = false,
    override var enterText: String? = null
) : ChatState {
    companion object

    fun fromOtherState(state: ChatState, silentEnter: Boolean? = null, enterText: String? = null): LocationsState = LocationsState(
        state.context,
        silentEnter ?: state.silentEnter,
        enterText ?: state.enterText
    )

}

@Deprecated("use new states")
data class StopState(
    override val context: ChatId,
    override var silentEnter: Boolean = false,
    override var enterText: String? = null
) : ChatState {
    companion object

    fun fromOtherState(state: ChatState, silentEnter: Boolean? = null, enterText: String? = null): StopState = StopState(
        state.context,
        silentEnter ?: state.silentEnter,
        enterText ?: state.enterText
    )
}

@Deprecated("use new states")
data class PostAdmissionInformationState(
    override val context: ChatId,
    override var silentEnter: Boolean = false,
    override var enterText: String? = null
) : ChatState {
    companion object

    fun fromOtherState(state: ChatState, silentEnter: Boolean? = null, enterText: String? = null): PostAdmissionInformationState = PostAdmissionInformationState(
        state.context,
        silentEnter ?: state.silentEnter,
        enterText ?: state.enterText
    )
}