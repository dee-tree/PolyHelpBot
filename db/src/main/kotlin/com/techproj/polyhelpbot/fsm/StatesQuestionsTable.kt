package com.techproj.polyhelpbot.fsm

import org.jetbrains.exposed.dao.id.IntIdTable

object StatesQuestionsTable : IntIdTable() {

    val description = varchar("state_description", 255).nullable()

    val isInitialState = bool("is_initial_state")
}