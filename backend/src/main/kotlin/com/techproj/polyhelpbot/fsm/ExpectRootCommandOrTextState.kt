package com.techproj.polyhelpbot.fsm

import com.techproj.polyhelpbot.*
import com.techproj.polyhelpbot.questions.repo.UserQuestionsRepository
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.strictlyOn
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.requests.abstracts.Request
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.extensions.makeString
import kotlinx.coroutines.flow.first

fun ExpectRootCommandOrAnswerState.Companion.register(fsmBuilder: BehaviourContextWithFSM<ChatState>, repository: UserQuestionsRepository) {
    with(fsmBuilder) {
        strictlyOn<ExpectRootCommandOrAnswerState, ChatState>() { state ->
            setMyCommands(
                BotCommand(LocationsState.Commands.where.command, "Подскажу, что где находится"),
                BotCommand(BaseCommands.help.command, "Не знаешь, как со мной общаться?")
            )

            if (!state.silentEnter) {
                sendMessage(
                    state.context.toChatId(),
                    ExpectRootCommandOrAnswerState.Text.chooseCommandOrTextAndIWillHelpYou,
                    parseMode = MarkdownParseMode,
                    replyMarkup = replyKeyboard(resizeKeyboard = true) {
                        row {
                            +SimpleKeyboardButton(LocationsState.Text.whereLocates.makeString())
                        }
                    })
            } else {
                state.silentEnter = false

                replyKeyboard(resizeKeyboard = true) {
                    row {
                        +SimpleKeyboardButton(LocationsState.Text.whereLocates.makeString())
                    }
                }
            }

            val userText =
                state.enterText?.let { enterText -> state.enterText = null; TextContent(enterText) }
                    ?: waitText().first()
//                    ?: waitText(filter = { it.chat.id == state.context.toChatId() }).first()

            val question = repository.searchQuestion(userText.text)

            question?.answer?.let { answer ->
                sendMessage(state.context.toChatId(), answer, parseMode = MarkdownParseMode,)
            } //?: run { sendMessage(state.context.toChatId(), "Я тебя не понимаю. Уточни свой вопрос") }

            when {
                userText.isCommand(LocationsState.Commands.where) -> LocationsState(state.context)
                userText.text == LocationsState.Text.whereLocates.makeString() -> LocationsState(state.context)
                userText.isCommand(BaseCommands.stop) -> StopState(state.context)

                question?.answer != null -> { sendMessage(state.context.toChatId(), "Что-то еще?"); state.toExpectRootCommandOrAnswerState(silentEnter = true) }
                question?.answerStateId != null -> ChatState.make(question.answerStateId!!, state.context, enterText = question.answerStateTextEnter)

                else -> { sendMessage(state.context.toChatId(), "Я тебя не понимаю. Уточни свой вопрос"); state.toExpectRootCommandOrAnswerState(silentEnter = true)}
            }

        }
    }

}

val ExpectRootCommandOrAnswerState.Companion.Text: ExpectRootCommandOrAnswerText
    get() = ExpectRootCommandOrAnswerText

val ExpectRootCommandOrAnswerState.Companion.Commands: ExpectRootCommandOrAnswerCommands
    get() = ExpectRootCommandOrAnswerCommands

object ExpectRootCommandOrAnswerText {
    const val chooseCommandOrTextAndIWillHelpYou = "Напиши мне свой вопрос или выбери предложенную команду, и я постараюсь тебе помочь"
}

object ExpectRootCommandOrAnswerCommands {
}
