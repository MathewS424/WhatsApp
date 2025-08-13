package com.midas.whatsapp.Model.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.midas.whatsapp.Model.data.User
import com.midas.whatsapp.util.CustomResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UpdateUserProfileImpl : UpdateUserProfile {

    private val database = FirebaseDatabase.getInstance()
    private val userReference = database.getReference("users")
    private val authRepository: AuthRepository = FirebaseAuthRepositoryImpl()


    override suspend fun updateUserProfile(user: User, updateMode: String): CustomResult<Unit> {
        val currentUserId = authRepository.getCurrentUser()?.uid
        if(currentUserId == null){
            return CustomResult.Failure(Exception("User not authenticated."))
        }

        return try{
            val userUpdates = when(updateMode){
                "USERNAME" -> hashMapOf<String, Any>(
                    "displayName" to user.displayName
                )
                "ABOUT" -> hashMapOf<String, Any>(
                    "about" to user.about
                )
                else -> hashMapOf()
            }

            if(userUpdates.isNotEmpty()){
                userReference.child(currentUserId).updateChildren(userUpdates).await()
                CustomResult.Success(Unit)
            }else{
                CustomResult.Success(Unit)
            }
        }catch (e: Exception){
            CustomResult.Failure(e)
        }
    }

    override suspend fun getUserProfile(): Flow<CustomResult<User>> = callbackFlow {
        val currentUserId = authRepository.getCurrentUser()?.uid ?: run {
            close(Exception("User not authenticated."))
            return@callbackFlow
        }

        val listener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if(user != null){
                    trySend(CustomResult.success(user))
                }else{
                    trySend(CustomResult.failure(Exception("User profile not found.")))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        userReference.child(currentUserId).addValueEventListener(listener)
        awaitClose { userReference.child(currentUserId).removeEventListener(listener) }
    }
}