package com.ajo.abarrotesOsorio.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * AuthResultState: Represents the different states of the authentication result.
 * This sealed class helps to handle the UI state in a clear and safe way.
 */
sealed class AuthResultState {
    object Loading : AuthResultState()
    data class Success(val user: FirebaseUser?) : AuthResultState()
    data class Error(val message: String) : AuthResultState()
}

/**
 * AuthRepository: Handles all Firebase Authentication operations.
 * This class abstracts the data source (Firebase Auth) from the ViewModel.
 */
class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Attempts to sign in a user with a given email and password.
     * @param email The user's email.
     * @param password The user's password.
     * @return A Flow that emits the current state of the sign-in process.
     */
    suspend fun signIn(email: String, password: String): Flow<AuthResultState> = flow {
        // Emit the loading state before starting the operation
        emit(AuthResultState.Loading)
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            // Emit the success state with the authenticated user
            emit(AuthResultState.Success(result.user))
        } catch (e: Exception) {
            // Emit the error state with the exception message
            emit(AuthResultState.Error(e.message ?: "An unknown authentication error occurred."))
        }
    }

    /**
     * Creates a new user with the provided email and password.
     * @param email The new user's email.
     * @param password The new user's password.
     * @return A Flow that emits the current state of the sign-up process.
     */
    suspend fun signUp(email: String, password: String): Flow<AuthResultState> = flow {
        // Emit the loading state before starting the operation
        emit(AuthResultState.Loading)
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            // Emit the success state with the newly created user
            emit(AuthResultState.Success(result.user))
        } catch (e: Exception) {
            // Emit the error state with the exception message
            emit(AuthResultState.Error(e.message ?: "An unknown authentication error occurred."))
        }
    }

    /**
     * Returns the currently authenticated user.
     * @return The FirebaseUser object, or null if no user is signed in.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
