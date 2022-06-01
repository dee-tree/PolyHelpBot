package com.techproj.polyhelpbot.fsm.connections

import com.techproj.polyhelpbot.fsm.StatesQuestionsTable
import com.techproj.polyhelpbot.questions.UserQuestionsTable
import org.jetbrains.exposed.dao.id.IntIdTable

object StatesConnectionsTable : IntIdTable() {
    val variant = varchar("variant", 255)
    val initialStateId = reference("initial_state_id", StatesQuestionsTable.id)
    val destinationStateId = reference("destination_state_id", StatesQuestionsTable.id)

    val answer = reference("answer", UserQuestionsTable).nullable()

}