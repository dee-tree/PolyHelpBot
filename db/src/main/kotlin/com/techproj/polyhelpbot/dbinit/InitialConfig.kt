package com.techproj.polyhelpbot.dbinit

import com.techproj.polyhelpbot.PhysicalPlace
import com.techproj.polyhelpbot.UserQuestion
import com.techproj.polyhelpbot.fsm.ExternalState
import com.techproj.polyhelpbot.fsm.ExternalStateId

@kotlinx.serialization.Serializable
data class InitialConfig(
    val places: List<PhysicalPlace>,
    val questions: List<UserQuestion>,

    val states: List<ExternalState>,
    val initialStateId: ExternalStateId
)
