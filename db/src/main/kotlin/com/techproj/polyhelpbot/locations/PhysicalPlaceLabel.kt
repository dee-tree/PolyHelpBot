package com.techproj.polyhelpbot.locations

import com.techproj.polyhelpbot.Label
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

internal object PhysicalPlaceLabelsTable : IdTable<Label>() {
    val name = varchar("name", 255).uniqueIndex()

    override val id: Column<EntityID<Label>> = varchar("id", 255).entityId()

    override val primaryKey: PrimaryKey = PrimaryKey(id)

}

internal class PhysicalPlaceLabelDAO(id: EntityID<Label>) : Entity<Label>(id) {
    companion object : EntityClass<Label, PhysicalPlaceLabelDAO>(PhysicalPlaceLabelsTable) {
        fun Label.newDAO(): PhysicalPlaceLabelDAO = PhysicalPlaceLabelDAO.new(this) {
            name = this@newDAO
        }

        @JvmName("newDAO1")
        fun newDAO(label: Label): PhysicalPlaceLabelDAO = PhysicalPlaceLabelDAO.new(label) {
            name = label
        }
    }

    var name by PhysicalPlaceLabelsTable.name

    fun toModel(): Label = name
}