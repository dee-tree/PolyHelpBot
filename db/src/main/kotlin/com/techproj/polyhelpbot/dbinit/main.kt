package com.techproj.polyhelpbot.dbinit

import com.techproj.polyhelpbot.BaseRepository
import com.techproj.polyhelpbot.locations.*
import com.techproj.polyhelpbot.locations.PhysicalPlaceDAO.Companion.newDAO
import com.techproj.polyhelpbot.states.StatesTable
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
            PhysicalPlacesTable,
            PhysicalPlaceLabelsTable,
            PhysicalPlaceLabelsAssociationTable,
            PhysicalPlaceKeywordsTable,
            PhysicalPlaceKeywordsAssociationTable,
        )

        SchemaUtils.create(
            // static data tables
            PhysicalPlacesTable,
            PhysicalPlaceLabelsTable,
            PhysicalPlaceLabelsAssociationTable,
            PhysicalPlaceKeywordsTable,
            PhysicalPlaceKeywordsAssociationTable,

            // dynamic tables
            StatesTable
        )


        config.places.forEach { place ->
            val dao = transaction { place.newDAO() }
            val keywords = transaction { place.keywords.map { PhysicalPlaceKeywordDAO.newDAO(it) } }
            val labels = transaction { place.labels.map { PhysicalPlaceLabelDAO.newDAO(it) } }

            dao.keywords = SizedCollection(keywords)
            dao.labels = SizedCollection(labels)
        }

    }

}