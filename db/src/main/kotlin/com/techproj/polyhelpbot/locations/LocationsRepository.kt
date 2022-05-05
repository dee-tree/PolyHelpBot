package com.techproj.polyhelpbot.locations

import com.techproj.polyhelpbot.PhysicalPlace

interface LocationsRepository {
    suspend fun getPlacesNames(): List<String>
    suspend fun getPlace(placeName: String): PhysicalPlace?
}