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
        return@newSuspendedTransaction PhysicalPlaceDAO.filter(1) { PhysicalPlacesTable.name eq placeName }.firstOrNull()?.toModel()

    }

}

fun DefaultLocationsRepository(dbConfiguration: String): LocationsRepository =
    LocationsRepositoryImpl(BaseRepository.database(dbConfiguration))