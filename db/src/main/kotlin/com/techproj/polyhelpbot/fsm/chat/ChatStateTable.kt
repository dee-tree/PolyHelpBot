package com.techproj.polyhelpbot.fsm.chat


import org.jetbrains.exposed.dao.id.LongIdTable

object ChatStateTable : LongIdTable() {
    val chatId = long("chatId").uniqueIndex()
    val stateId = integer("stateId").nullable()

}