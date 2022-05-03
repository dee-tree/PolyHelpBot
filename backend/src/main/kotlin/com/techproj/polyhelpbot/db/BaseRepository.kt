package com.techproj.polyhelpbot.db

import com.google.firebase.database.*
import dev.inmo.tgbotapi.extensions.utils.formatting.buildEntities
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.MessageEntity.textsources.botCommand
import kotlinx.coroutines.suspendCancellableCoroutine

internal abstract class BaseRepository(protected val db: FirebaseDatabase) {

    protected val rootRef: DatabaseReference = db.reference


    protected suspend fun DatabaseReference.getSnapshot(): DataSnapshot = suspendCancellableCoroutine { cont ->
        val callback = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cont.resumeWith(Result.success(snapshot))
            }

            override fun onCancelled(error: DatabaseError?) {
                cont.resumeWith(Result.failure(error!!.toException()))
            }
        }


        this.addListenerForSingleValueEvent(callback)
    }


    protected suspend fun <T> DatabaseReference.setValue(value: T) = suspendCancellableCoroutine<Unit> { cont ->
        val callback = DatabaseReference.CompletionListener { error, ref ->
            cont.resumeWith(error?.let { Result.failure(it.toException()) } ?: Result.success(Unit))
        }

        this.setValue(value, callback)
    }

    protected suspend fun <T> DatabaseReference.getValue(clazz: Class<T>): T? = suspendCancellableCoroutine { cont ->
        val callback = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(clazz)
                cont.resumeWith(Result.success(data))
            }

            override fun onCancelled(error: DatabaseError?) {
                cont.resumeWith(Result.failure(error!!.toException()))
            }
        }


        this.addListenerForSingleValueEvent(callback)
    }

}

object BasePath {
    const val chats = "chats"
    const val static = "static"
    fun chat(chatId: ChatId) = "$chats/${chatId.chatId}"
}

object BaseText {
    val back = buildEntities {
        +"Назад"
    }
}

object BaseCommands {
    val start = botCommand("start")
    val stop = botCommand("stop")
    val back = botCommand("back")
}