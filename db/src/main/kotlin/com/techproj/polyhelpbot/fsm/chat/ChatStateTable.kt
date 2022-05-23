package com.techproj.polyhelpbot.fsm.chat


import com.techproj.polyhelpbot.ChatId
import com.techproj.polyhelpbot.questions.UserQuestionsKeywordsTable.entityId
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object ChatStateTable : LongIdTable() {
    val chatId = long("chatId").uniqueIndex()
    val stateId = integer("stateId").nullable()


//    override val id: Column<EntityID<ChatId>> = reference("chatId", ChatStateTable.chatId)//chatId.entityId()
}