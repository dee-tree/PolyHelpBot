package com.techproj.polyhelpbot.fsm

import com.techproj.polyhelpbot.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

@kotlinx.serialization.Serializable
data class ExternalState(
    @SerialName("id")
    val externalStateId: ExternalStateId,
    @SerialName("variants")
    val variants: List<ExternalStateConnection>,
    @SerialName("description")
    val description: String? = null
) {

    // 0x7FFFFFFF - to get only positive hash
    @Transient
    val id: StateId = (externalStateId.value.hashCode() and 0x7FFFFFFF).toStateId()

    init {
        require(variants.isNotEmpty())
    }
}

@JvmInline
@kotlinx.serialization.Serializable
value class ExternalStateId(val value: String) {

}

@kotlinx.serialization.Serializable
data class ExternalStateConnection(
    @SerialName("variant")
    val variant: String,
    @SerialName("stateId")
    val externalStateId: ExternalStateId,
    @SerialName("answerId")
    val answerId: AnswerId? = null
) {
    // 0x7FFFFFFF - to get only positive hash
    @Transient
    val stateId: StateId = (externalStateId.value.hashCode() and 0x7FFFFFFF).toStateId()

}