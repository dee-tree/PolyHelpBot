package com.techproj.polyhelpbot.fsm

import com.techproj.polyhelpbot.StateId
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

internal class StateQuestionsDAO(id: EntityID<Int>) : IntEntity(id) {
    var description by StatesQuestionsTable.description
    var isInitialState by StatesQuestionsTable.isInitialState

    companion object : IntEntityClass<StateQuestionsDAO>(StatesQuestionsTable) {
        fun newDAO(stateId: StateId, description: String? = null, isInitialState: Boolean = false): StateQuestionsDAO =
            StateQuestionsDAO.new(stateId.value) {
                this.description = description
                this.isInitialState = isInitialState
            }
    }
}