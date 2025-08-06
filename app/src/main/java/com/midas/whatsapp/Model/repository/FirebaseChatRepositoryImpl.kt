package com.midas.whatsapp.Model.repository


import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

import com.midas.whatsapp.Model.data.Message
import com.midas.whatsapp.Model.data.User
import com.midas.whatsapp.util.CustomResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseChatRepositoryImpl : ChatRepository {

    private val database = FirebaseDatabase.getInstance()
    private val userReference = database.getReference("users")
    private val chatsReference = database.getReference("chats")
    private val authRepository: AuthRepository = FirebaseAuthRepositoryImpl()
    private val currentUserId = authRepository.getCurrentUser()?.uid

    override suspend fun sendMessage(message: Message): CustomResult<Unit> {
        return try {
            val chatRoomId = getChatRoomId(message.senderId, message.receiverId)
            setMessageCount(message, chatRoomId)
            val newMessageRef = chatsReference.child(chatRoomId).child("messages").push()
            val messageWithId = message.copy(id = newMessageRef.key ?: "")
            newMessageRef.setValue(messageWithId).await()
            CustomResult.success(Unit)
        } catch (e: Exception) {
            CustomResult.failure(e)
        }
    }

    override fun getMessages(chatRoomId: String): Flow<CustomResult<List<Message>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                snapshot.children.forEach { messageSnapshot ->
                    val message = messageSnapshot.getValue(Message::class.java)
                    message?.let { messages.add(it) }
                }
                messages.sortBy { it.timestamp }    // Ensure messages are sorted by Time
                trySend(CustomResult.success(messages)).isSuccess   // Offer the list of messages
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(CustomResult.failure(error.toException())).isFailure    // Offer the error
            }

        }
        chatsReference.child(chatRoomId).child("messages")
            .addValueEventListener(listener)    // Listen for all messages in the room
        awaitClose {
            chatsReference.child(chatRoomId).child("messages").removeEventListener(listener)
        }   // Remove listener when flow is cancelled


    }

    override suspend fun saveUser(user: User): CustomResult<Unit> {
        return try {
            userReference.child(user.uid).setValue(user).await()
            CustomResult.success(Unit)
        } catch (e: Exception) {
            CustomResult.failure(e)
        }
    }

    override fun getUsers(): Flow<CustomResult<List<User>>> = callbackFlow {
        val listener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                 val users = mutableListOf<User>()
                snapshot.children.forEach { userSnapshot ->
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let { users.add(it) }
                }
                trySend(CustomResult.success(users)).isSuccess

            }
            override fun onCancelled(error: DatabaseError) {
                trySend(CustomResult.failure(error.toException())).isFailure
            }
        }
        userReference.addValueEventListener(listener)
        awaitClose { userReference.removeEventListener(listener) }
    }

    override fun getRecentChattedUsersId(): Flow<CustomResult<List<String>>> = callbackFlow    {
        val listener = object:  ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val recentUserChatList = mutableListOf<String>()
                snapshot.children.forEach{ chatSnapshot ->
                    val chatId = chatSnapshot.key
                    val otherUserId = getOtherUserId(chatId.toString())
                    otherUserId.let { recentUserChatList.add(it) }
                }
                trySend(CustomResult.success(recentUserChatList))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(CustomResult.failure(error.toException())).isFailure
            }
        }
        chatsReference.addValueEventListener(listener)
        awaitClose{chatsReference.removeEventListener(listener)}
    }

    override suspend fun getUsersByIdsFromAll(userIds: List<String>): Flow<CustomResult<List<User>>> = callbackFlow{

            val listener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                 val users = mutableListOf<User>()
                snapshot.children.forEach { userSnapshot ->
                    val user = userSnapshot.getValue(User::class.java)
                    if(user != null && userIds.contains(user.uid)){
                        user.let { users.add(user) }
                    }
                }
                trySend(CustomResult.success(users)).isSuccess

            }

            override fun onCancelled(error: DatabaseError) {
                trySend(CustomResult.failure(error.toException())).isFailure
            }

        }
        userReference.addValueEventListener(listener)
        awaitClose { userReference.removeEventListener(listener) }


    }

    override fun getChatRoomId(userOneId: String, userTwoId: String): String {
        return if (userOneId < userTwoId) "${userOneId}_${userTwoId}" else "${userTwoId}_${userOneId}"
    }

    override fun getMessageCount(userId: String, callback: (String) -> Unit) {
        val currentUserId = authRepository.getCurrentUser()?.uid
        if (currentUserId == null) {
            callback("") // or "0" as default
            return
        }

        chatsReference.child(getChatRoomId(currentUserId, userId))
            .child("${userId}_receiverCount")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.value?.toString() ?: ""
                    Log.d("messages", "Count: $count")
                    callback(count)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback("") // or handle error appropriately
                }
            })
    }

    override fun resetMessageCount(userId: String) {

    }


    private fun getOtherUserId(chatRoomId: String): String{
        val userIdOne = chatRoomId.split("_")[0]
        val userIdTwo = chatRoomId.split("_")[1]
        return if(currentUserId == userIdOne){
            userIdTwo
        }else if (currentUserId == userIdTwo) {
            userIdOne
        }else{
            ""
        }
    }

    suspend fun setMessageCount(message: Message, chatRoomId: String){
        val receiverMessageCountRef = chatsReference.child(chatRoomId).child("${message.senderId}_receiverCount")
         suspendCancellableCoroutine<Unit> { cont->
             receiverMessageCountRef.runTransaction(object: Transaction.Handler{
                 override fun doTransaction(currentData: MutableData): Transaction.Result {
                      val currentCount = currentData.getValue<Int>() ?: 0
                     currentData.value = currentCount + 1
                     return Transaction.success(currentData)

                 }

                 override fun onComplete(
                     error: DatabaseError?,
                     committed: Boolean,
                     currentData: DataSnapshot?
                 ) {
                     if(error != null){
                         cont.resumeWithException(error.toException())
                     }else{
                         cont.resume(Unit)
                     }
                 }
             })
         }
    }




}