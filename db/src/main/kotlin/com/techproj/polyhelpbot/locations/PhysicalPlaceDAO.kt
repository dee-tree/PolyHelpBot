package com.techproj.polyhelpbot.locations

import com.techproj.polyhelpbot.PhysicalPlace
import com.techproj.polyhelpbot.location.Geolocation
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

internal class PhysicalPlaceDAO(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, PhysicalPlaceDAO>(PhysicalPlacesTable) {

        fun PhysicalPlace.newDAO(): PhysicalPlaceDAO = PhysicalPlaceDAO.new {
            name = this@newDAO.name
            address = this@newDAO.address
            help = this@newDAO.help
            latitude = this@newDAO.location.latitude
            longitude = this@newDAO.location.longitude
        }
    }

    var name by PhysicalPlacesTable.name
    var address by PhysicalPlacesTable.address
    var help by PhysicalPlacesTable.help

    var labels by PhysicalPlaceLabelDAO via PhysicalPlaceLabelsAssociationTable
    var keywords by PhysicalPlaceKeywordDAO via PhysicalPlaceKeywordsAssociationTable

    var latitude by PhysicalPlacesTable.latitude
    var longitude by PhysicalPlacesTable.longitude


    fun toModel(): PhysicalPlace = PhysicalPlace(
        name,
        address,
        help,
        labels.map { it.toModel() },
        keywords.map { it.toModel() },
        Geolocation(latitude, longitude)
    )
}
