package com.techproj.polyhelpbot.dbinit

import com.techproj.polyhelpbot.BaseRepository
import com.techproj.polyhelpbot.ChatId
import com.techproj.polyhelpbot.ExternalChatState
import com.techproj.polyhelpbot.fsm.StateQuestionsDAO
import com.techproj.polyhelpbot.fsm.StatesQuestionsTable
import com.techproj.polyhelpbot.fsm.chat.ChatStateTable
import com.techproj.polyhelpbot.fsm.chat.StateRepositoryImpl
import com.techproj.polyhelpbot.fsm.connections.StateConnectionsDAO
import com.techproj.polyhelpbot.fsm.connections.StatesConnectionsTable
import com.techproj.polyhelpbot.locations.*
import com.techproj.polyhelpbot.locations.PhysicalPlaceDAO.Companion.newDAO
import com.techproj.polyhelpbot.locations.PhysicalPlaceKeywordsTable
import com.techproj.polyhelpbot.locations.PhysicalPlaceLabelsTable
import com.techproj.polyhelpbot.locations.PhysicalPlacesTable
import com.techproj.polyhelpbot.questions.UserQuestionDAO.Companion.newDAO
import com.techproj.polyhelpbot.questions.UserQuestionKeywordDAO
import com.techproj.polyhelpbot.questions.UserQuestionKeywordsAssociationTable
import com.techproj.polyhelpbot.questions.UserQuestionsKeywordsTable
import com.techproj.polyhelpbot.questions.UserQuestionsTable
import com.techproj.polyhelpbot.toStateId
import dev.inmo.micro_utils.coroutines.launchSynchronously
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun main(args: Array<String>) {
    // first arg - db configuration
    // second arg - db init file
    val dbConfiguration = args[0]
    val dbInitFilePath = args[1]

    val config = Json.decodeFromStream<InitialConfig>(File(dbInitFilePath).inputStream())

    println("config: $config")

    BaseRepository.database(dbConfiguration)

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.drop(
            StatesQuestionsTable,
            StatesConnectionsTable,

            PhysicalPlacesTable,
            PhysicalPlaceLabelsTable,
            PhysicalPlaceLabelsAssociationTable,
            PhysicalPlaceKeywordsTable,
            PhysicalPlaceKeywordsAssociationTable,

            UserQuestionsTable,
            UserQuestionsKeywordsTable,
            UserQuestionKeywordsAssociationTable,

//            ChatStateTable

        )

        SchemaUtils.create(
            // static data tables
            PhysicalPlacesTable,
            PhysicalPlaceLabelsTable,
            PhysicalPlaceLabelsAssociationTable,
            PhysicalPlaceKeywordsTable,
            PhysicalPlaceKeywordsAssociationTable,

            UserQuestionsTable,
            UserQuestionsKeywordsTable,
            UserQuestionKeywordsAssociationTable,

            StatesQuestionsTable,
            StatesConnectionsTable,

            // dynamic tables
            ChatStateTable
        )


        config.places.forEach { place ->
            val placeDAO = transaction { place.newDAO() }
            val placeKeywords = transaction { place.keywords.map { PhysicalPlaceKeywordDAO.findById(it) ?: PhysicalPlaceKeywordDAO.newDAO(it) } }
            val placeLabels = transaction { place.labels.map { PhysicalPlaceLabelDAO.findById(it) ?: PhysicalPlaceLabelDAO.newDAO(it) } }

            placeDAO.keywords = SizedCollection(placeKeywords)
            placeDAO.labels = SizedCollection(placeLabels)
        }

        config.questions.forEach { question ->
            val questionDAO = transaction { question.newDAO() }
            val questionKeywords = transaction { question.keywords.map { UserQuestionKeywordDAO.findById(it) ?: UserQuestionKeywordDAO.newDAO(it) } }

            questionDAO.keywords = SizedCollection(questionKeywords)
        }


        val states = config.states.associateWith { state ->
            transaction {
                StateQuestionsDAO.newDAO(state.id, state.description, config.initialStateId == state.externalStateId)
            }
        }

        config.states.forEach { state ->
            require(state.id.value >= 0) { "External state id must be non-negative!" }
            transaction {
                SizedCollection(state.variants.map { variant ->
                    StateConnectionsDAO.newDAO(
                        state.id,
                        variant.variant,
                        variant.stateId,
                        variant.answerId
                    )
                })
            }

        }


    }

}