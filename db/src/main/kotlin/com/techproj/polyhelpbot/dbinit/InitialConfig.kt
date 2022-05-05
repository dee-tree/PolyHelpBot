package com.techproj.polyhelpbot.dbinit

import com.techproj.polyhelpbot.Label
import com.techproj.polyhelpbot.PhysicalPlace

@kotlinx.serialization.Serializable
data class InitialConfig(
    val places: List<PhysicalPlace>,
    val placesLabels: List<Label>
)
