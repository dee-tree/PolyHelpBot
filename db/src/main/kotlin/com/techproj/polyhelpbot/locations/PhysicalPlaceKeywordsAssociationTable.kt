package com.techproj.polyhelpbot.locations

import org.jetbrains.exposed.sql.Table

/**
 * For many-to-many association
 */
object PhysicalPlaceKeywordsAssociationTable : Table() {
    val physicalPlace = reference("physicalPlace", PhysicalPlacesTable)
    val keyword = reference("keyword", PhysicalPlaceKeywordsTable)

    override val primaryKey: PrimaryKey = PrimaryKey(physicalPlace, keyword, name="PK_PhysicalPlaceKeywordsAssociation_pp_kw")
}