package com.techproj.polyhelpbot


import com.techproj.polyhelpbot.locations.LocationsRepository
import com.techproj.polyhelpbot.locations.LocationsRepositoryImpl
import com.techproj.polyhelpbot.states.StateRepository
import com.techproj.polyhelpbot.states.StateRepositoryImpl
import org.jetbrains.exposed.sql.Database

internal class RepositoryImpl(
    db: Database,
    locationsRepository: LocationsRepository,
    stateRepository: StateRepository
) : BaseRepository(db),
    Repository,
    LocationsRepository by locationsRepository,
    StateRepository by stateRepository {
}

fun DefaultRepository(dbConfiguration: String): Repository {
    val db = BaseRepository.database(dbConfiguration)
    return RepositoryImpl(db, LocationsRepositoryImpl(db), StateRepositoryImpl(db))
}