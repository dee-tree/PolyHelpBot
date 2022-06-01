package com.techproj.polyhelpbot

import com.techproj.polyhelpbot.location.Geolocation
import kotlinx.serialization.Serializable

@Serializable
data class PhysicalPlace(
    val name: String,
    val address: String,
    val location: Geolocation
) {
}

typealias Keyword = String
