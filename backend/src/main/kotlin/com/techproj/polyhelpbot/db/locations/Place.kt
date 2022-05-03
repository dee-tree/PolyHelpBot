package com.techproj.polyhelpbot.db.locations

data class Place(
    val name: String,
    val address: String,
    val help: String? = null,
    val labels: List<Label> = emptyList(),
    val latitude: Double,
    val longitude: Double
) {

    @JvmInline
    value class Label(val value: String)
}
