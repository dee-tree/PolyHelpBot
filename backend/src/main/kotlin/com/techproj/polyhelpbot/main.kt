package com.techproj.polyhelpbot

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import com.techproj.polyhelpbot.db.BaseCommands
import com.techproj.polyhelpbot.db.RepositoryImpl
import com.techproj.polyhelpbot.db.locations.LocationsRepositoryImpl
import com.techproj.polyhelpbot.db.state.StateRepositoryImpl
import com.techproj.polyhelpbot.fsm.*
import dev.inmo.micro_utils.fsm.common.managers.DefaultStatesManager
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndFSMAndStartLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.extensions.utils.extensions.parseCommandsWithParams
import dev.inmo.tgbotapi.types.MessageEntity.textsources.BotCommandTextSource
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File


fun main(args: Array<String>) {
    val botToken = args[0]
    val fbOptions = args[1]
    val dbPath = args[2]

    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(File(fbOptions).inputStream()))
        .setDatabaseUrl(dbPath)
        .build()

    FirebaseApp.initializeApp(options)

    val db = FirebaseDatabase.getInstance()
    val repo = RepositoryImpl(db, LocationsRepositoryImpl(db), StateRepositoryImpl(db))
    val fsmStatesRepo = StatesManagerRepo(remoteRepo = repo)

    runBlocking {

        telegramBotWithBehaviourAndFSMAndStartLongPolling<ChatState>(
            botToken,
            CoroutineScope(Dispatchers.IO),
            statesManager = DefaultStatesManager(repo = fsmStatesRepo)
        ) {

            strictlyOn<StopState> {
                sendMessage(
                    it.context,
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
                    println("restored chain is null`")
                    if (it.content.isCommand(BaseCommands.start)) {
                        startChain(ExpectRootCommandOrTextState(it.chat.id))
                    }
                }
            }


            ExpectRootCommandOrTextState.register(this)
            LocationsState.register(this, repo)

        }.second.join()
    }
}


fun TextContent.isCommand(command: BotCommandTextSource) = command.command in this.parseCommandsWithParams().keys