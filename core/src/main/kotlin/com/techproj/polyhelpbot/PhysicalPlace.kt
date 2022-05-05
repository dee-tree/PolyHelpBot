package com.techproj.polyhelpbot

import com.techproj.polyhelpbot.location.Geolocation
import kotlinx.serialization.Serializable

@Serializable
data class PhysicalPlace(
    val name: String,
    val address: String,
    val help: String? = null,
    val labels: List<Label> = emptyList(),
    val keywords: List<Keyword>,
    val location: Geolocation
) {

}

typealias Label = String
typealias Keyword = String
