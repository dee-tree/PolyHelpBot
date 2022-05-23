package com.techproj.polyhelpbot

import com.techproj.polyhelpbot.locations.LocationsRepository
import com.techproj.polyhelpbot.questions.repo.UserQuestionsRepository
import com.techproj.polyhelpbot.fsm.chat.StateRepository

interface Repository : LocationsRepository, StateRepository, UserQuestionsRepository {

}
