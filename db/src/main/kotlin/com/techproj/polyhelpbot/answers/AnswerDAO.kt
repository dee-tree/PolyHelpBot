package com.techproj.polyhelpbot.answers

import com.techproj.polyhelpbot.Answer
import com.techproj.polyhelpbot.locations.PhysicalPlaceDAO
import com.techproj.polyhelpbot.locations.PhysicalPlaceDAO.Companion.newDAO
import com.techproj.polyhelpbot.locations.PhysicalPlacesTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

internal class AnswerDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AnswerDAO>(AnswersTable) {
        fun Answer.newDAO(): AnswerDAO = AnswerDAO.new {
            when (this@newDAO) {
                is Answer.Text -> {
                    this.text = this@newDAO.content
                }
                is Answer.Location -> {
                    this.text = this@newDAO.text?.content
                    val location = PhysicalPlaceDAO.find { PhysicalPlacesTable.name eq this@newDAO.location.name }.firstOrNull() ?: this@newDAO.location.newDAO()
                    this.location = location// PhysicalPlaceDAO.find { PhysicalPlacesTable.name eq this@newDAO.location.name }.first()
                }
            }
        }
    }

    var text by AnswersTable.text
    var location by PhysicalPlaceDAO optionalReferencedOn AnswersTable.location


    fun toModel(): Answer = when {
        location != null -> Answer.Location(location!!.toModel(), text?.let { Answer.Text(it) })
        else -> Answer.Text(text!!)
    }
}