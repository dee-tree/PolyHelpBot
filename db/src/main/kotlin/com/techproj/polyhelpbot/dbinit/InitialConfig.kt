package com.techproj.polyhelpbot.dbinit

import com.techproj.polyhelpbot.PhysicalPlace
import com.techproj.polyhelpbot.UserQuestion

@kotlinx.serialization.Serializable
data class InitialConfig(
    val places: List<PhysicalPlace>,
    val questions: List<UserQuestion>
)
