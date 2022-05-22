package com.techproj.polyhelpbot.questions

import com.techproj.polyhelpbot.UserQuestion
import com.techproj.polyhelpbot.toStateId
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

internal class UserQuestionDAO(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, UserQuestionDAO>(UserQuestionsTable) {
        fun UserQuestion.newDAO(): UserQuestionDAO = UserQuestionDAO.new(this.question) {
            question = this@newDAO.question
            answer = this@newDAO.answer
            answerStateId = this@newDAO.answerStateId?.value
        }
    }

    var question by UserQuestionsTable.question
    var answerStateId by UserQuestionsTable.answerStateId
    var answer by UserQuestionsTable.answer

    var keywords by UserQuestionKeywordDAO via UserQuestionKeywordsAssociationTable


    fun toModel(): UserQuestion = UserQuestion(
        question,
        keywords.map { it.toModel() },
        answerStateId?.toStateId(),
        null, // TODO: fix it
        answer
    )


}