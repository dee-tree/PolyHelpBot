package com.techproj.polyhelpbot.locations

import com.techproj.polyhelpbot.*
import com.techproj.polyhelpbot.BaseRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

internal class LocationsRepositoryImpl(db: Database) : BaseRepository(db), LocationsRepository {
    override suspend fun getPlacesNames(): List<String> {
        return PhysicalPlaceDAO.getAll().map { it.name }
    }

    override suspend fun getPlace(placeName: String): PhysicalPlace? = newSuspendedTransaction {
        val concretePlaceNameModel =
            PhysicalPlaceDAO.filter(1) { PhysicalPlacesTable.name eq placeName }.firstOrNull()?.toModel()

        concretePlaceNameModel?.let { return@newSuspendedTransaction it }

        val nearestPlace = PhysicalPlaceDAO.getAll().minByOrNull { placeName.minDistance(it.keywords.map { kw -> kw.toModel() }) }?.toModel()

        nearestPlace?.let { if (it.similarWithSearch(placeName)) return@newSuspendedTransaction it }

        return@newSuspendedTransaction null

    }

}

private fun PhysicalPlaceDAO.similarWithSearch(str: String): Boolean = str.isSimilar(this.keywords.map { it.toModel() })
private fun PhysicalPlace.similarWithSearch(str: String): Boolean = str.isSimilar(this.keywords)
fun DefaultLocationsRepository(dbConfiguration: String): LocationsRepository =
    LocationsRepositoryImpl(BaseRepository.database(dbConfiguration))