package com.midas.whatsapp.Model.repository

import com.midas.whatsapp.Model.data.User
import com.midas.whatsapp.util.CustomResult
import kotlinx.coroutines.flow.Flow

interface UpdateUserProfile {

    suspend fun updateUserProfile(user: User, updateMode: String): CustomResult<Unit>
    suspend fun getUserProfile(): Flow<CustomResult<User>>
}