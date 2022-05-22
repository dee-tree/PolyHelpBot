package com.techproj.polyhelpbot

@kotlinx.serialization.Serializable
data class UserQuestion(
    val question: String,
    val keywords: List<Keyword>,

    val answerStateId: StateId? = null,
    val answerStateTextEnter: String? = null,

    val answer: String? = null
)
