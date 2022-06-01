package com.techproj.polyhelpbot.dbinit

@kotlinx.serialization.Serializable
/**
 * @param location is name of *place*
 */
internal data class AnswerObject(
    val text: String? = null,
    val location: String? = null
)