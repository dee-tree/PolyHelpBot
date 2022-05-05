package com.techproj.polyhelpbot.locations

import org.jetbrains.exposed.sql.Table

/**
 * For many-to-many association
 */
object PhysicalPlaceLabelsAssociationTable : Table() {
    val physicalPlace = reference("physicalPlace", PhysicalPlacesTable)//.primaryKey(0)
    val label = reference("label", PhysicalPlaceLabelsTable)//.primaryKey(1)

    override val primaryKey: PrimaryKey = PrimaryKey(
        physicalPlace,
        label, name="PK_PhysicalPlaceLabelsAssociation_pp_lbl")

}