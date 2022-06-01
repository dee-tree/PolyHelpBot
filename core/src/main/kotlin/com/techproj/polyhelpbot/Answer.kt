package com.techproj.polyhelpbot

@kotlinx.serialization.Serializable
sealed class Answer() {

    @kotlinx.serialization.Serializable
    data class Text(val content: String): Answer() {

    }

    @kotlinx.serialization.Serializable
    data class Location(val location: PhysicalPlace, val text: Text? = null): Answer()
}