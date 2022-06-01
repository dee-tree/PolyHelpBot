package com.techproj.polyhelpbot.questions

import com.techproj.polyhelpbot.Answer
import com.techproj.polyhelpbot.UserQuestion
import com.techproj.polyhelpbot.answers.AnswerDAO
import com.techproj.polyhelpbot.answers.AnswerDAO.Companion.newDAO
import com.techproj.polyhelpbot.answers.AnswersTable
import com.techproj.polyhelpbot.dbinit.QuestionObject
import com.techproj.polyhelpbot.locations.PhysicalPlaceDAO
import com.techproj.polyhelpbot.locations.PhysicalPlacesTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

internal class UserQuestionDAO(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, UserQuestionDAO>(UserQuestionsTable) {
        fun UserQuestion.newDAO(): UserQuestionDAO = UserQuestionDAO.new(this.question) {
            question = this@newDAO.question
            val answer = this@newDAO.answer

            val answerDAO =
                when (answer) {
                    is Answer.Text -> AnswerDAO.find { AnswersTable.text eq answer.content }
                    is Answer.Location -> {
                        val place = PhysicalPlaceDAO.find { PhysicalPlacesTable.name eq answer.location.name }.firstOrNull()
                        place?.let { AnswerDAO.find { AnswersTable.text eq answer.text?.content } }
                    }
                }?.firstOrNull() ?: answer.newDAO()

            this.answer = answerDAO
        }

        fun QuestionObject.newDAO(): UserQuestionDAO = UserQuestion(question, keywords, null, when {
            answer.location != null -> Answer.Location(PhysicalPlaceDAO.find { PhysicalPlacesTable.name eq answer.location }.first().toModel(), answer.text?.let { Answer.Text(it) })
            else -> Answer.Text(answer.text!!)
        }).newDAO()
    }

    var question by UserQuestionsTable.question

    var answer by AnswerDAO referencedOn UserQuestionsTable.answer

    var keywords by UserQuestionKeywordDAO via UserQuestionKeywordsAssociationTable


    fun toModel(): UserQuestion = UserQuestion(
        question,
        keywords.map { it.toModel() },
        null, // TODO: fix it
        answer.toModel()
    )


}