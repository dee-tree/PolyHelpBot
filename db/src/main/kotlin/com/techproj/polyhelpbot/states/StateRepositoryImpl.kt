package com.techproj.polyhelpbot.states

import com.techproj.polyhelpbot.BaseRepository
import com.techproj.polyhelpbot.ChatId
import com.techproj.polyhelpbot.ChatState
import com.techproj.polyhelpbot.states.StateDAO.Companion.newDAO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

internal class StateRepositoryImpl(
    db: Database
) : BaseRepository(db), StateRepository {

    init {
        transaction {
            SchemaUtils.create(StatesTable)
        }
    }

    override suspend fun saveState(state: ChatState): Unit = newSuspendedTransaction {
        addLogger(StdOutSqlLogger)
        state.newDAO()
    }

    override suspend fun removeState(state: ChatState): Unit = newSuspendedTransaction {
        addLogger(StdOutSqlLogger)
        StatesTable.deleteWhere { StatesTable.chatId eq state.context }
    }

    override suspend fun getState(chatId: ChatId): ChatState? =
        StateDAO.filter(1) { StatesTable.chatId eq chatId }.firstOrNull()?.toModel()

    override suspend fun getAllStates(): List<ChatState> {
        return StateDAO.getAll().mapNotNull { it.toModel()?.also { it.silentEnter = true } }
    }

}

fun DefaultStateRepository(dbConfiguration: String): StateRepository =
    StateRepositoryImpl(BaseRepository.database(dbConfiguration))