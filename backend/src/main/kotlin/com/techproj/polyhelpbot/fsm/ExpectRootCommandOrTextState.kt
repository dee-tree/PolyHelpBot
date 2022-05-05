package com.techproj.polyhelpbot.fsm

import com.techproj.polyhelpbot.*
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.strictlyOn
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.extensions.makeString

fun ExpectRootCommandOrAnswerState.Companion.register(fsmBuilder: BehaviourContextWithFSM<ChatState>) {
    with(fsmBuilder) {
        strictlyOn<ExpectRootCommandOrAnswerState, ChatState>() {
            println("In expect state!")
            setMyCommands(BotCommand(LocationsState.Commands.where.command, "Подскажу, что где находится"))

            if (!it.silentEnter) {
                sendMessage(
                    it.context.toChatId(),
                    ExpectRootCommandOrAnswerState.Text.chooseCommandOrTextAndIWillHelpYou,
                    replyMarkup = replyKeyboard(resizeKeyboard = true) {
                        row {
                            +SimpleKeyboardButton(LocationsState.Text.whereLocates.makeString())
                        }
                    })
            } else {
                it.silentEnter = false

                replyKeyboard(resizeKeyboard = true) {
                    row {
                        +SimpleKeyboardButton(LocationsState.Text.whereLocates.makeString())
                    }
                }
            }


            val userText =
                it.enterText?.let { enterText -> it.enterText = null; TextContent(enterText) }
                    ?: waitText().first()

            when {
                userText.isCommand(LocationsState.Commands.where) -> LocationsState(it.context)
                userText.text == LocationsState.Text.whereLocates.makeString() -> LocationsState(it.context)
                userText.isCommand(BaseCommands.stop) -> StopState(it.context)
                else -> it
            }

        }
    }

}

val ExpectRootCommandOrAnswerState.Companion.Text: ExpectRootCommandOrAnswerText
    get() = ExpectRootCommandOrAnswerText

val ExpectRootCommandOrAnswerState.Companion.Commands: ExpectRootCommandOrAnswerCommands
    get() = ExpectRootCommandOrAnswerCommands

object ExpectRootCommandOrAnswerText {
    val chooseCommandOrTextAndIWillHelpYou = "Выбери предложенную команду, и я постараюсь тебе помочь"
}

object ExpectRootCommandOrAnswerCommands {
}
