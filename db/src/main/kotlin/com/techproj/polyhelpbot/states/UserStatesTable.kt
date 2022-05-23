package com.techproj.polyhelpbot.states

import org.jetbrains.exposed.dao.id.LongIdTable

object UserStatesTable : LongIdTable() {
    val chatId = long("chatId").uniqueIndex()//it was .primaryKey()
    val stateId = integer("stateId").nullable()

}