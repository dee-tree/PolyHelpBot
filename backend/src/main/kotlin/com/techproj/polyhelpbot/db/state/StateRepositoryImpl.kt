package com.techproj.polyhelpbot.db.state

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.techproj.polyhelpbot.db.BasePath
import com.techproj.polyhelpbot.db.BaseRepository
import com.techproj.polyhelpbot.fsm.ChatState
import com.techproj.polyhelpbot.fsm.StateId
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.toChatId
import kotlinx.coroutines.suspendCancellableCoroutine
import com.techproj.polyhelpbot.db.state.StateRepository.Path as StatePath

internal class StateRepositoryImpl(
    db: FirebaseDatabase
) : BaseRepository(db), StateRepository {

    override suspend fun saveState(state: ChatState) =
        rootRef.child(StatePath.state(state.context)).setValue(state.toRepositoryStoredState())

    override suspend fun removeState(state: ChatState) = rootRef.child(StatePath.state(state.context)).setValue(null)

    override suspend fun getState(chatId: ChatId): ChatState? =
        rootRef.child(StatePath.state(chatId)).getValue<RepositoryStoredState>(RepositoryStoredState::class.java)
            ?.toChatState()

    override suspend fun getAllStates(): List<ChatState> = suspendCancellableCoroutine { cont ->
        val callback = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map { it.child("state").getValue(RepositoryStoredState::class.java) }
                cont.resumeWith(Result.success(emptyList()))
            }

            override fun onCancelled(error: DatabaseError?) {
                cont.resumeWith(Result.failure(error!!.toException()))
            }
        }


        rootRef.child(BasePath.chats).addListenerForSingleValueEvent(callback)
    }
}

private fun ChatState.toRepositoryStoredState() = RepositoryStoredState(stateId.value, context.chatId)
private data class RepositoryStoredState(val stateId: Long = -1, val chatId: Long = -1) {
    fun toChatState(): ChatState {
        return ChatState.make(StateId(stateId), chatId.toChatId())
    }
}