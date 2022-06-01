package com.techproj.polyhelpbot.locations

import org.jetbrains.exposed.dao.id.IntIdTable

internal object PhysicalPlacesTable : IntIdTable() {

    val name = varchar("name", 255)
    val address = varchar("address", 255)

    val latitude = double("latitude")
    val longitude = double("longitude")
}
