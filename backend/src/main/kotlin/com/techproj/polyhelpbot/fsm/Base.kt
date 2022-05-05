package com.techproj.polyhelpbot.fsm

import dev.inmo.tgbotapi.extensions.utils.formatting.buildEntities
import dev.inmo.tgbotapi.types.MessageEntity.textsources.botCommand

object BaseText {
    val back = buildEntities {
        +"Назад"
    }
}

object BaseCommands {
    val start = botCommand("start")
    val stop = botCommand("stop")
    val back = botCommand("back")
}