package com.techproj.polyhelpbot.db.locations

import com.techproj.polyhelpbot.db.BasePath

interface LocationsRepository {
    suspend fun getPlacesNames(): List<String>
    suspend fun getPlace(placeName: String): Place?

    object Path {
        const val places = "${BasePath.static}/places"
    }
}