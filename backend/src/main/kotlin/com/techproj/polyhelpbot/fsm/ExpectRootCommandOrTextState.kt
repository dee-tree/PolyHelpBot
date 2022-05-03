package com.techproj.polyhelpbot.fsm

import com.techproj.polyhelpbot.db.BaseCommands
import com.techproj.polyhelpbot.isCommand
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSMBuilder
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.strictlyOn
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.utils.extensions.makeString

data class ExpectRootCommandOrTextState(
    override val context: ChatId,
    override var silentEnter: Boolean = false,
    override var enterText: String? = null
) : ChatState {

    companion object {

        fun register(fsmBuilder: BehaviourContextWithFSMBuilder<ChatState>) {
            with(fsmBuilder) {
                strictlyOn<ExpectRootCommandOrTextState, ChatState>() {
                    setMyCommands(BotCommand(LocationsState.Commands.where.command, "Подскажу, что где находится"))

                    if (!it.silentEnter) {
                        sendMessage(
                            it.context,
                            Text.chooseCommandOrTextAndIWillHelpYou,
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

    }


    object Text {
        val chooseCommandOrTextAndIWillHelpYou = "Выбери предложенную команду, и я постараюсь тебе помочь"
    }

    object Commands {
    }
}