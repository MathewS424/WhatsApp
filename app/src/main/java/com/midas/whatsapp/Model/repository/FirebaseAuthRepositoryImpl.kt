package com.midas.whatsapp.Model.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.midas.whatsapp.util.CustomResult
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl: AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun registerUser(email: String, password: String): CustomResult<FirebaseUser> {

        return try {

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if(firebaseUser != null){
                CustomResult.success(firebaseUser)
            }else{
                CustomResult.failure(Exception("Registration failed: Firebase user is null."))
            }
        }catch (e:Exception){
            // Catch any exceptions (e.g., weak password, email already in use)
            CustomResult.failure(e)
        }
    }

    override suspend fun loginUser(email: String, password: String): CustomResult<FirebaseUser> {
         return try{
             val authResult = auth.signInWithEmailAndPassword(email, password).await()
             val firebaseUser = authResult.user
             if(firebaseUser!= null){
                 CustomResult.success(firebaseUser)
             }else{
                 CustomResult.failure(Exception("Login failed: Firebase user is null."))
             }
         }catch (e: Exception){
             CustomResult.failure(e)
         }
    }

    override fun getCurrentUser(): FirebaseUser? {
         return auth.currentUser
    }

    override suspend fun signOut(): CustomResult<Unit> {
         return try{
             auth.signOut()
             CustomResult.success(Unit)
         }catch (e: Exception){
             CustomResult.failure(e)
         }
    }
}