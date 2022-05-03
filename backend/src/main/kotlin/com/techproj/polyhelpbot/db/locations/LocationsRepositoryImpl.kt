package com.techproj.polyhelpbot.db.locations

import com.google.firebase.database.FirebaseDatabase
import com.techproj.polyhelpbot.db.BaseRepository
import com.techproj.polyhelpbot.db.locations.LocationsRepository.Path as LocationsPath

internal class LocationsRepositoryImpl(db: FirebaseDatabase) : BaseRepository(db), LocationsRepository {
    override suspend fun getPlacesNames(): List<String> {
        return rootRef.child(LocationsPath.places).getSnapshot().children.map { it.child("name").getValue(String::class.java) }
    }

    override suspend fun getPlace(placeName: String): Place? {
        val map = rootRef.child(LocationsPath.places).getSnapshot().children.firstOrNull { it.child("name").getValue(String::class.java) == placeName }?.value as? HashMap<*, *>

        return map?.let {
            Place(
                it["name"] as String,
                it["address"] as String,
                it["help"] as? String,
                latitude = it["latitude"] as Double,
                longitude = it["longitude"] as Double
            )
        }
    }
}