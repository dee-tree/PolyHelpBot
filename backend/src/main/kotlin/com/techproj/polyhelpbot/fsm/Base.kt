package com.techproj.polyhelpbot.fsm

import dev.inmo.tgbotapi.extensions.utils.formatting.buildEntities
import dev.inmo.tgbotapi.types.MessageEntity.textsources.botCommand

object BaseText {
    val back = buildEntities {
        +"Назад"
    }

    const val helloMessage = "Привет! Ты можешь задать мне любой интересующий тебя вопрос, связанный с учебной или внеучебной деятельностью. Это может быть расположение учебным корпусов, вопросы, связанные со стипендиями, учебным планом, ПРОФОМ и не только."
    const val helpMessage = "*Что я умею?*\n\n" +
            "1) Ты можешь меня спрашивать о *расположении* учебных корпусов и административных зданий, например \"гз\" или \"главное здание\"\n" +
            "2) Я знаю ответ на множество вопросов, связанных с учебной деятельностью. Например: \"где посмотреть учебный план\", \"как получить справку об обучении\" и много других!\n" +
            "3) Смело задавай вопросы, связанные с внеучебной деятельностью, например: \"что такое проф\" или \"как вступить в проф\", или даже \"сборные политеха\"\n" +
            "\\* Если я не могу найти вопрос, попробуй его перефразировать. Удачи!"
}

object BaseCommands {
    val start = botCommand("start")
    val stop = botCommand("stop")
    val back = botCommand("back")
    val help = botCommand("help")
}