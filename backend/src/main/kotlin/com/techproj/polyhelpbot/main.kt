package com.techproj.polyhelpbot

import com.techproj.polyhelpbot.fsm.BaseCommands
import com.techproj.polyhelpbot.fsm.BaseText
import com.techproj.polyhelpbot.fsm.StatesManagerRepo
import com.techproj.polyhelpbot.fsm.register
import dev.inmo.micro_utils.fsm.common.managers.DefaultStatesManager
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndFSMAndStartLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.extensions.utils.extensions.parseCommandsWithParams
import dev.inmo.tgbotapi.types.MessageEntity.textsources.BotCommandTextSource
import dev.inmo.tgbotapi.types.ParseMode.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.toChatId
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


fun main(args: Array<String>) {
    // first arg - bot token
    // second arg - db configuration
    val botToken = args[0]
    val dbConfiguration = args[1]

    val repo = DefaultRepository(dbConfiguration)
    val fsmStatesRepo = StatesManagerRepo(remoteRepo = repo)

    runBlocking {

        telegramBotWithBehaviourAndFSMAndStartLongPolling<ChatState>(
            botToken,
            CoroutineScope(Dispatchers.IO),
            statesManager = DefaultStatesManager(repo = fsmStatesRepo),
            defaultExceptionsHandler = {
                System.err.println("Error occurred in bot ${it.printStack()}")
            }

        ) {
            strictlyOn<StopState> {
                sendMessage(
                    it.context.toChatId(),
                    "Общение с ботом прекращено. Введите ${BaseCommands.start.asText}, чтобы вновь общаться со мной"
                )
                null
            }


            onText(initialFilter = { fsmStatesRepo.getContextState(it.chat.id) == null }) {
                fsmStatesRepo.getContextState(it.chat.id)?.let { restoredChain ->
                    println("in restored chain: $restoredChain")
                    restoredChain.silentEnter = true
                    restoredChain.enterText = it.content.text
                    startChain(restoredChain)
                } ?: run {
                    println("restored chain is null")
                    if (it.content.isCommand(BaseCommands.start)) {
                        sendMessage(it.chat.id, BaseText.helloMessage, parseMode = MarkdownParseMode)
                        startChain(ExpectRootCommandOrAnswerState(it.chat.id.toChatId(), silentEnter = true))
                    }
                }
            }

            onCommand(BaseCommands.help.command) {
                sendMessage(it.chat.id, BaseText.helpMessage, parseMode = MarkdownParseMode)
            }


            ExpectRootCommandOrAnswerState.register(this, repo)
            LocationsState.register(this, repo)

        }.second.join()
    }
}


fun TextContent.isCommand(command: BotCommandTextSource) = command.command in this.parseCommandsWithParams().keys