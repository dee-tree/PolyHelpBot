package com.techproj.polyhelpbot.fsm.connections

import com.techproj.polyhelpbot.AnswerId
import com.techproj.polyhelpbot.StateConnection
import com.techproj.polyhelpbot.StateId
import com.techproj.polyhelpbot.fsm.ExternalStateConnection
import com.techproj.polyhelpbot.fsm.StateQuestionsDAO
import com.techproj.polyhelpbot.toStateId
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

internal class StateConnectionsDAO(id: EntityID<Int>) : IntEntity(id)  {

    companion object : IntEntityClass<StateConnectionsDAO>(StatesConnectionsTable) {
        fun newDAO(
            initialStateId: StateId,
            variant: String,
            destinationStateId: StateId,
            answerId: AnswerId? = null
        ): StateConnectionsDAO {
            return StateConnectionsDAO.new {
                this.initialStateId = StateQuestionsDAO[initialStateId.value]
                this.variant = variant
                this.destinationStateId = StateQuestionsDAO[destinationStateId.value]
                // TODO Write answer id set: this.answerId =
            }
        }

        fun newDAO(
            initialStateId: StateId,
            externalStateConnection: ExternalStateConnection
        ): StateConnectionsDAO {
            return StateConnectionsDAO.new {
                this.initialStateId = StateQuestionsDAO[initialStateId.value]
                this.variant = externalStateConnection.variant
                this.destinationStateId = StateQuestionsDAO[externalStateConnection.stateId.value]
                // TODO Write answer id set: this.answerId =
            }
        }
    }

    var variant by StatesConnectionsTable.variant
    var initialStateId by StateQuestionsDAO referencedOn StatesConnectionsTable.initialStateId
    var destinationStateId by StateQuestionsDAO referencedOn StatesConnectionsTable.destinationStateId
    var answerId by StatesConnectionsTable.answerId

    fun toModel(): StateConnection = StateConnection(variant, destinationStateId.id.value.toStateId() , answerId?.let { AnswerId(it.value) })
}