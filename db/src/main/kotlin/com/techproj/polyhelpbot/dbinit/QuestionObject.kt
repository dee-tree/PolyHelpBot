package com.techproj.polyhelpbot.dbinit

import com.techproj.polyhelpbot.Keyword


@kotlinx.serialization.Serializable
internal data class QuestionObject(
    val question: String,
    val keywords: List<Keyword>,
    val answer: AnswerObject
)
