package com.midas.whatsapp.Model.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.midas.whatsapp.Model.data.Message
import com.midas.whatsapp.Model.data.User
import com.midas.whatsapp.util.CustomResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseChatRepositoryImpl : ChatRepository {

    private val database = FirebaseDatabase.getInstance()
    private val userReference = database.getReference("users")
    private val chatsReference = database.getReference("chats")
    private val authRepository: AuthRepository = FirebaseAuthRepositoryImpl()
    private val currentUserId = authRepository.getCurrentUser()?.uid

    override suspend fun sendMessage(message: Message): CustomResult<Unit> {
        return try {
            val chatRoomId = getChatRoomId(message.senderId, message.receiverId)

            val newMessageRef = chatsReference.child(chatRoomId).push()
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
        chatsReference.child(chatRoomId)
            .addValueEventListener(listener)    // Listen for all messages in the room
        awaitClose {
            chatsReference.child(chatRoomId).removeEventListener(listener)
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
}