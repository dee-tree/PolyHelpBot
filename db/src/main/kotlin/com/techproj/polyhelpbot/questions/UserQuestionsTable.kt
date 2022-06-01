package com.techproj.polyhelpbot.questions

import com.techproj.polyhelpbot.answers.AnswersTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object UserQuestionsTable : IdTable<String>() {

    val question = varchar("question", 255)

    val answer = reference("answer", AnswersTable)

    override val id: Column<EntityID<String>> = varchar("id", 255).entityId()
    override val primaryKey: PrimaryKey = PrimaryKey(id)

}