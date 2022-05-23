package com.techproj.polyhelpbot.fsm

import com.techproj.polyhelpbot.ExternalChatState
import com.techproj.polyhelpbot.NewChatState
import com.techproj.polyhelpbot.StopChatState
import com.techproj.polyhelpbot.fsm.chat.StateRepository
import com.techproj.polyhelpbot.fsm.chat.nextExternalStateViaVariant
import com.techproj.polyhelpbot.isCommand
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.strictlyOn
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.toChatId
import kotlinx.coroutines.flow.first

fun ExternalChatState.Companion.register(fsmBuilder: BehaviourContextWithFSM<NewChatState>, repo: StateRepository) {
    with(fsmBuilder) {
        strictlyOn<ExternalChatState, NewChatState>() {state ->
            println("we are on $state")

            if (!state.silentEnter) {
                sendMessage(state.context.toChatId(), "Таак...",  replyMarkup = replyKeyboard(resizeKeyboard = true) {
                    state.variants.forEach {
                        +SimpleKeyboardButton(it.variant)
                    }
                })
            }  else {
                state.silentEnter = false
            }


            val userAnswer = state.enterText?.let { enterText -> state.enterText = null; TextContent(enterText) }
                ?: waitText().first()
//                ?: waitText(filter = { it.chat.id == state.context.toChatId() }).first()

            val nextStateViaVariant = state.nextExternalStateViaVariant(repo, userAnswer.text) //?: state

            when {
                userAnswer.isCommand(BaseCommands.stop) -> StopChatState(state.context)
                nextStateViaVariant != null -> {
                    println("go to state ${nextStateViaVariant}"); nextStateViaVariant }

                else -> {
                    println("lol null next state")
                    println("Stay at state ${state}") ; state}
            }

        }
    }
}