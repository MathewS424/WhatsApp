package com.midas.whatsapp.Model.repository

import com.google.firebase.auth.FirebaseUser
import com.midas.whatsapp.util.CustomResult

interface AuthRepository {

    suspend fun registerUser(email:String, password: String): CustomResult<FirebaseUser>
    suspend fun loginUser(email: String, password: String): CustomResult<FirebaseUser>
    fun getCurrentUser(): FirebaseUser?
    suspend fun signOut(): CustomResult<Unit>
}