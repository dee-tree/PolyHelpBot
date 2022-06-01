package com.techproj.polyhelpbot.fsm

import com.techproj.polyhelpbot.*
import com.techproj.polyhelpbot.fsm.chat.StateRepository
import com.techproj.polyhelpbot.fsm.chat.nextExternalStateViaVariant
import com.techproj.polyhelpbot.questions.repo.UserQuestionsRepository
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.strictlyOn
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.toChatId
import kotlinx.coroutines.flow.first

fun ExternalChatState.Companion.register(fsmBuilder: BehaviourContextWithFSM<NewChatState>, repo: StateRepository, answersRepo: UserQuestionsRepository) {
    with(fsmBuilder) {
        strictlyOn<ExternalChatState, NewChatState>() {state ->
            println("we are on $state")

            if (!state.silentEnter) {
                val msg = sendMessage(state.context.toChatId(), "Я в ожидании следующей команды",  replyMarkup = replyKeyboard(resizeKeyboard = true) {
                    state.variants.forEach {
                        +SimpleKeyboardButton(it.variant)
                    }
                })
            }  else {
                state.silentEnter = false
            }


            val userAnswer = state.enterText?.let { enterText -> state.enterText = null; TextContent(enterText) }
                ?: waitText().first()

            val variant = state.findVariant(userAnswer.text)
            variant?.answerId?.let { answerId ->
                val answer = answersRepo.searchQuestion(answerId.value)!!.answer
                sendMessage(state.context, answer)

            }
            val nextStateViaVariant = variant?.let { state.nextExternalStateViaVariant(repo, it) }
            nextStateViaVariant ?: sendMessage(state.context.toChatId(), "Выбери команду из предложенных")
            if (nextStateViaVariant?.stateId == state.stateId) {
                nextStateViaVariant.silentEnter = true
            }

            when {
                userAnswer.isCommand(BaseCommands.stop) -> StopChatState(state.context)
                nextStateViaVariant != null -> nextStateViaVariant

                else -> { state.copy(silentEnter = true) }
            }

        }
    }
}