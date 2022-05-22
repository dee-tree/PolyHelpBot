package com.techproj.polyhelpbot.questions.repo

import com.techproj.polyhelpbot.BaseRepository
import com.techproj.polyhelpbot.UserQuestion
import com.techproj.polyhelpbot.isSimilar
import com.techproj.polyhelpbot.minDistance
import com.techproj.polyhelpbot.questions.UserQuestionDAO
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

internal class UserQuestionsRepositoryImpl(db: Database) : BaseRepository(db), UserQuestionsRepository {

    override suspend fun searchQuestion(question: String): UserQuestion? = newSuspendedTransaction {
        val nearestRequest = UserQuestionDAO.getAll().minByOrNull { question.minDistance(it.keywords.map { kw -> kw.toModel() }) }?.toModel()

        nearestRequest?.let { if (it.similarWithSearch(question)) return@newSuspendedTransaction it }

        return@newSuspendedTransaction null
    }
}

private fun UserQuestion.similarWithSearch(str: String): Boolean = str.isSimilar(this.keywords)


fun DefaultUserQuestionsRepository(dbConfiguration: String): UserQuestionsRepository =
    UserQuestionsRepositoryImpl(BaseRepository.database(dbConfiguration))