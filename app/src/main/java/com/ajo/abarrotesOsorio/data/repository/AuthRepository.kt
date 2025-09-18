package com.ajo.abarrotesOsorio.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.google.firebase.auth.FirebaseUser

sealed class AuthResultState<out T> {
    object Loading : AuthResultState<Nothing>()
    data class Success<out T>(val data: T) : AuthResultState<T>()
    data class Error(val message: String) : AuthResultState<Nothing>()
}

class AuthRepository {

    private val auth: FirebaseAuth = Firebase.auth

    fun signUp(email: String, password: String): Flow<AuthResultState<FirebaseUser>> = flow {
        emit(AuthResultState.Loading)
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).result
            val user = result.user
            if (user != null) {
                emit(AuthResultState.Success(user))
            } else {
                emit(AuthResultState.Error("User creation failed, but no specific error was reported."))
            }
        } catch (e: Exception) {
            emit(AuthResultState.Error(e.message ?: "An unknown error occurred during sign up."))
        }
    }

    fun signIn(email: String, password: String): Flow<AuthResultState<FirebaseUser>> = flow {
        emit(AuthResultState.Loading)
        try {
            val result = auth.signInWithEmailAndPassword(email, password).result
            val user = result.user
            if (user != null) {
                emit(AuthResultState.Success(user))
            } else {
                emit(AuthResultState.Error("Sign in failed, but no specific error was reported."))
            }
        } catch (e: Exception) {
            emit(AuthResultState.Error(e.message ?: "An unknown error occurred during sign in."))
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun signOut(): Flow<AuthResultState<Unit>> = flow {
        emit(AuthResultState.Loading)
        try {
            auth.signOut()
            emit(AuthResultState.Success(Unit))
        } catch (e: Exception) {
            emit(AuthResultState.Error(e.message ?: "An unknown error occurred during logout."))
        }
    }
}
