package com.midas.whatsapp.Model.repository

import com.midas.whatsapp.Model.data.Message
import com.midas.whatsapp.Model.data.User
import com.midas.whatsapp.util.CustomResult
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun sendMessage(message: Message): CustomResult<Unit>
    fun getMessages(chatRoomId: String): Flow<CustomResult<List<Message>>>  // Use flow for real-time updates

    suspend fun saveUser(user: User): CustomResult<Unit>  // To save/Update user profile
    fun getUsers(): Flow<CustomResult<List<User>>>  // To get a list of all users
    fun getRecentChattedUsersId(): Flow<CustomResult<List<String>>>

    suspend fun getUsersByIdsFromAll(userIds: List<String>): Flow<CustomResult<List<User>>>

    fun getChatRoomId(userOneId: String, userTwoId: String): String

    fun getMessageCount(otherUserId: String): Flow<Int>
    fun resetMessageCount(otherUserId: String)


}