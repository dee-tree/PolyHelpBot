package com.techproj.polyhelpbot

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

internal abstract class BaseRepository(protected val database: Database) {

    companion object {
        internal fun database(config: String): Database = Database.connect(config, driver = "org.h2.Driver")
    }


    protected suspend fun <ID : Comparable<ID>, T : Entity<ID>> EntityClass<ID, T>.getAll(): List<T> =
        newSuspendedTransaction(
            Dispatchers.IO
        ) {
            addLogger(StdOutSqlLogger)

            this@getAll.all().toList()
        }


    protected suspend fun <ID : Comparable<ID>, T : Entity<ID>> EntityClass<ID, T>.filter(
        limit: Int? = null,
        predicate: SqlExpressionBuilder.() -> Op<Boolean>
    ): List<T> = newSuspendedTransaction {
        addLogger(StdOutSqlLogger)

        val filtered = this@filter.find { predicate(this) }.apply { limit?.let { limit(it) } }
        filtered.toList()
    }


}