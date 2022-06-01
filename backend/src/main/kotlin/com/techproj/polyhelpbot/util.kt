package com.techproj.polyhelpbot

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.api.send.sendStaticLocation
import dev.inmo.tgbotapi.extensions.utils.formatting.boldln
import dev.inmo.tgbotapi.extensions.utils.formatting.buildEntities
import dev.inmo.tgbotapi.types.MessageIdentifier
import dev.inmo.tgbotapi.types.buttons.KeyboardMarkup
import dev.inmo.tgbotapi.types.message.ParseMode
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.toChatId

fun dev.inmo.tgbotapi.types.ChatId.toChatId(): com.techproj.polyhelpbot.ChatId = this.chatId

suspend fun TelegramBot.sendMessage(
    chatId: ChatId,
    answer: Answer,
    parseMode: ParseMode? = null,
    replyToMessageId: MessageIdentifier? = null,
    allowSendingWithoutReply: Boolean? = null,
    replyMarkup: KeyboardMarkup? = null
): ContentMessage<*> {
    return when (answer) {
        is Answer.Text -> {
            sendMessage(
                chatId.toChatId(),
                answer.content,
                parseMode = parseMode,
                replyToMessageId = replyToMessageId,
                allowSendingWithoutReply = allowSendingWithoutReply,
                replyMarkup = replyMarkup
            )
        }
        is Answer.Location -> {
            val locationMessage = sendStaticLocation(
                chatId.toChatId(),
                latitude = answer.location.location.latitude,
                longitude = answer.location.location.longitude
            )

            answer.text?.content?.let { text ->
                sendMessage(chatId.toChatId(), buildEntities {
                    boldln(answer.location.name)
                    +"\n"
                    +answer.location.address
                    +"\n"
                    +text
                }, replyToMessageId = locationMessage.messageId)
            } ?: locationMessage
        }
    }
}