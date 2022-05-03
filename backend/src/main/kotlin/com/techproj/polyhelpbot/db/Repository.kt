package com.techproj.polyhelpbot.db

import com.techproj.polyhelpbot.db.locations.LocationsRepository
import com.techproj.polyhelpbot.db.state.StateRepository

interface Repository : LocationsRepository, StateRepository {

}
