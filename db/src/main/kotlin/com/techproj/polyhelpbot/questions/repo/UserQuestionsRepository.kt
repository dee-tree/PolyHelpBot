package com.techproj.polyhelpbot.questions.repo

import com.techproj.polyhelpbot.UserQuestion

interface UserQuestionsRepository {

    suspend fun searchQuestion(question: String): UserQuestion?
}