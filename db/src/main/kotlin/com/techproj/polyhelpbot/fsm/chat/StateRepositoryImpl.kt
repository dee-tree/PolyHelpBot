package com.techproj.polyhelpbot.fsm.chat

import com.techproj.polyhelpbot.*
import com.techproj.polyhelpbot.fsm.StateQuestionsDAO
import com.techproj.polyhelpbot.fsm.StatesQuestionsTable
import com.techproj.polyhelpbot.fsm.chat.ChatStateDAO.Companion.newChatStateDAO
import com.techproj.polyhelpbot.fsm.connections.StateConnectionsDAO
import com.techproj.polyhelpbot.fsm.connections.StatesConnectionsTable
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

internal class StateRepositoryImpl(
    db: Database
) : BaseRepository(db), StateRepository {

    init {
        transaction {
            SchemaUtils.create(ChatStateTable)
        }
    }

    override suspend fun saveState(state: NewChatState): Unit = newSuspendedTransaction {
        addLogger(StdOutSqlLogger)
        println("save state: ${state}")
        state.newChatStateDAO()
        Unit
    }.also {
        newSuspendedTransaction {
            println("State saved: ${ChatStateDAO.getAll().find { it.toModel() == state } != null}")
        }
    }

    override suspend fun removeState(state: NewChatState): Unit = newSuspendedTransaction {
        addLogger(StdOutSqlLogger)
        ChatStateTable.deleteWhere { ChatStateTable.chatId eq state.context }
        Unit
    }.also {
        newSuspendedTransaction {
            println("After remove state ${state} it is not in db: ${ChatStateDAO.find { ChatStateTable.chatId eq state.context }.empty()}")
        }
    }

    override suspend fun getState(chatId: ChatId): NewChatState? = newSuspendedTransaction {

        ChatStateDAO.filter(1) { ChatStateTable.chatId eq chatId }.firstOrNull()?.toModel()
    }.also {
        newSuspendedTransaction {
            println("successfully get state: ${ChatStateDAO.filter(1) { ChatStateTable.chatId eq chatId }.firstOrNull()?.toModel()}")
        }
    }

    override suspend fun getAllStates(): List<NewChatState> = newSuspendedTransaction {
        ChatStateDAO.getAll().mapNotNull { it.toModel()?.apply { silentEnter = true } }
    }.also {
        newSuspendedTransaction {
            println("successfully get all states: ${ChatStateDAO.getAll().mapNotNull { it.toModel()?.apply { silentEnter = true } }}")
        }
    }

    override suspend fun getInitialExternalState(chatId: ChatId): ExternalChatState = newSuspendedTransaction {
//        StatesQuestionsTable.select { StatesQuestionsTable.isInitialState eq true }.single().
        val initialStateId = StateQuestionsDAO.filter(1) { StatesQuestionsTable.isInitialState eq true }.single().id

        return@newSuspendedTransaction getExternalState(chatId, initialStateId.value.toStateId())
//        return@newSuspendedTransaction getExternalState(chatId, 0.toStateId())
    }.also {
        newSuspendedTransaction {
            val initialStateId = StateQuestionsDAO.filter(1) { StatesQuestionsTable.isInitialState eq true }.single().id

            println("successfully get initial external state: ${getExternalState(chatId, initialStateId.value.toStateId())}")
        }
    }

    override suspend fun getExternalState(chatId: ChatId, stateId: StateId): ExternalChatState =
        newSuspendedTransaction {
            val connections = StateConnectionsDAO.filter { StatesConnectionsTable.initialStateId eq stateId.value }
            return@newSuspendedTransaction ExternalChatState(
                chatId,
                stateId,
                connections.map { it.toModel() },
                StateQuestionsDAO[stateId.value].description
            )
        }.also {
            newSuspendedTransaction {
                val connections = StateConnectionsDAO.filter { StatesConnectionsTable.initialStateId eq stateId.value }
                println("successfully get external state: ${ExternalChatState(
                    chatId,
                    stateId,
                    connections.map { it.toModel() },
                    StateQuestionsDAO[stateId.value].description
                )}")
            }
        }

}

fun DefaultStateRepository(dbConfiguration: String): StateRepository =
    StateRepositoryImpl(BaseRepository.database(dbConfiguration))