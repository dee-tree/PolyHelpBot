package com.techproj.polyhelpbot.fsm

import com.techproj.polyhelpbot.ChatState
import com.techproj.polyhelpbot.LocationsState
import com.techproj.polyhelpbot.isCommand
import com.techproj.polyhelpbot.locations.LocationsRepository
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.api.send.sendStaticLocation
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.strictlyOn
import dev.inmo.tgbotapi.extensions.utils.formatting.boldln
import dev.inmo.tgbotapi.extensions.utils.formatting.buildEntities
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.types.MessageEntity.textsources.botCommand
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.location.StaticLocation
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.extensions.makeString


fun LocationsState.Companion.register(
    fsmBuilder: BehaviourContextWithFSM<ChatState>,
    repo: LocationsRepository
) {
    with(fsmBuilder) {
        strictlyOn<LocationsState, ChatState> {
            val places = repo.getPlacesNames()

            setMyCommands(BotCommand(BaseCommands.back.command, "Вернуться на шаг назад"))

            if (!it.silentEnter) {
                this.sendMessage(
                    it.context.toChatId(),
                    "Я могу сказать, что где находится! Выбери из списка интересующее место или введи ${BaseCommands.back.asText}, чтобы вернуться на шаг назад",
                    replyMarkup = replyKeyboard(resizeKeyboard = true, oneTimeKeyboard = true) {
                        (places).forEach { place ->
                            +SimpleKeyboardButton(place)
                        }
                        +SimpleKeyboardButton(BaseText.back.makeString())
                    }
                )
            } else {
                it.silentEnter = false
            }


            val text = it.enterText?.let { enterText -> it.enterText = null; TextContent(enterText) }
                ?: waitText().first()


            val place = repo.getPlace(text.text)

            place?.let { place ->

                val locationMessage = sendStaticLocation(
                    it.context.toChatId(),
                    StaticLocation(place.location.longitude, place.location.latitude),
                    disableNotification = true,
                )
                sendMessage(
                    it.context.toChatId(),
                    buildEntities {
                        boldln(place.name)
                        +"\n"
                        +place.address
                        +"\n"
                        place.help?.let { help -> +help }
                    },
                    replyToMessageId = locationMessage.messageId
                )
            }

            when {
                text.isCommand(BaseCommands.back) || text.text == BaseText.back.makeString() -> {
                    it.toExpectRootCommandOrAnswerState()
                }
                text.isCommand(BaseCommands.stop) -> it.toStopState()
                place != null -> {
                    it.toExpectRootCommandOrAnswerState(silentEnter = true)
                }
                else -> it
            }

        }
    }
}


val LocationsState.Companion.Text: LocationsText
    get() = LocationsText

val LocationsState.Companion.Commands: LocationsCommands
    get() = LocationsCommands


object LocationsText {
    val whereLocates = buildEntities {
        +"Где находится..."
    }
}

object LocationsCommands {
    val where = botCommand("where")
}

