package com.techproj.polyhelpbot.answers

import com.techproj.polyhelpbot.locations.PhysicalPlacesTable
import org.jetbrains.exposed.dao.id.IntIdTable

object AnswersTable: IntIdTable() {
    val text = varchar("text", 255).nullable()
    val location = reference("location", PhysicalPlacesTable).nullable()
}