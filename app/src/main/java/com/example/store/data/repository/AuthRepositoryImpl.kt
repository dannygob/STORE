package com.example.store.data.repository

import com.example.store.domain.model.LoginResult
import com.example.store.domain.model.UserRole // Keep for now, but role handling will change
import com.example.store.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    // The concept of UserRole will need to be handled differently,
    // typically by storing roles in Firestore/Realtime Database against the user's UID.
    // For now, LoginResult will not contain the role directly from Firebase Auth.
    override suspend fun login(email: String, password: String): Result<LoginResult> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            // TODO: Fetch user role from Firestore/RTDB based on firebaseAuth.currentUser.uid
            // For now, returning a default or placeholder role if needed, or simplify LoginResult
            Result.success(LoginResult(UserRole.USER)) // Placeholder
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(Exception("Usuario no encontrado o deshabilitado."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Contraseña incorrecta."))
        } catch (e: Exception) {
            Result.failure(Exception("Error de inicio de sesión: ${e.localizedMessage}"))
        }
    }

    // UserRole handling needs to be re-evaluated.
    // Firebase Auth does not store custom roles. This should be done in Firestore/RTDB.
    // The 'role' parameter is removed for now from this direct signature.
    // It should be passed to a subsequent step like 'saveUserDetails(uid, role)'.
    override suspend fun register(email: String, password: String): Result<Unit> { // Signature matches interface
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            // TODO: After successful registration, save user role and other details to Firestore/RTDB
            // val userId = firebaseAuth.currentUser?.uid
            // if (userId != null) { /* saveUserRole(userId, role) */ }
            Result.success(Unit)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(Exception("El correo electrónico ya está en uso."))
        } catch (e: Exception) {
            Result.failure(Exception("Error de registro: ${e.localizedMessage}"))
        }
    }

    override suspend fun recoverPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(Exception("Correo electrónico no encontrado."))
        } catch (e: Exception) {
            Result.failure(Exception("Error al enviar correo de recuperación: ${e.localizedMessage}"))
        }
    }

    override suspend fun signOut(): Result<Unit> { // Added override and return type
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al cerrar sesión: ${e.localizedMessage}"))
        }
    }

    // getCurrentUser might be useful
    fun getCurrentUser() = firebaseAuth.currentUser

    fun getAuthStateFlow(): kotlinx.coroutines.flow.Flow<com.google.firebase.auth.FirebaseUser?> = kotlinx.coroutines.flow.callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser).isSuccess
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }
}