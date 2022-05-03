package com.techproj.polyhelpbot.db

import com.google.firebase.database.FirebaseDatabase
import com.techproj.polyhelpbot.db.locations.LocationsRepository
import com.techproj.polyhelpbot.db.state.StateRepository

internal class RepositoryImpl(
    db: FirebaseDatabase,
    locationsRepository: LocationsRepository,
    stateRepository: StateRepository
) : BaseRepository(db),
    Repository,
    LocationsRepository by locationsRepository,
    StateRepository by stateRepository {
}
