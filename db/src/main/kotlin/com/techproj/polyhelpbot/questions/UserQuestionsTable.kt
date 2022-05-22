package com.techproj.polyhelpbot.questions

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object UserQuestionsTable : IdTable<String>() {

    val question = varchar("question", 255)

    val answer = varchar("answer", 700).nullable()

    val answerStateId = integer("answerStateId").nullable()


    override val id: Column<EntityID<String>> = varchar("id", 255).entityId()
    override val primaryKey: PrimaryKey = PrimaryKey(id)

}