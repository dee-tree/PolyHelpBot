package com.techproj.polyhelpbot


import com.techproj.polyhelpbot.fsm.chat.StateRepository
import com.techproj.polyhelpbot.fsm.chat.StateRepositoryImpl
import com.techproj.polyhelpbot.locations.LocationsRepository
import com.techproj.polyhelpbot.locations.LocationsRepositoryImpl
import com.techproj.polyhelpbot.questions.repo.UserQuestionsRepository
import com.techproj.polyhelpbot.questions.repo.UserQuestionsRepositoryImpl
import org.jetbrains.exposed.sql.Database

internal class RepositoryImpl(
    db: Database,
    locationsRepository: LocationsRepository,
    stateRepository: StateRepository,
    userQuestionsRepository: UserQuestionsRepository
) : BaseRepository(db),
    Repository,
    LocationsRepository by locationsRepository,
    StateRepository by stateRepository,
    UserQuestionsRepository by userQuestionsRepository {
}

fun DefaultRepository(dbConfiguration: String): Repository {
    val db = BaseRepository.database(dbConfiguration)
    return RepositoryImpl(db, LocationsRepositoryImpl(db), StateRepositoryImpl(db), UserQuestionsRepositoryImpl(db))
}