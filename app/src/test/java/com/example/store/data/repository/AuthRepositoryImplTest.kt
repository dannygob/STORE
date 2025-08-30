package com.example.store.data.repository

import android.content.Context
import com.example.store.data.local.dao.UserDao
import com.example.store.data.local.entity.UserEntity
import com.example.store.domain.model.UserRole
import com.example.store.util.NetworkChecker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

@ExperimentalCoroutinesApi
class AuthRepositoryImplTest {

    private lateinit var authRepository: AuthRepositoryImpl
    private val auth: FirebaseAuth = mockk()
    private val firestore: FirebaseFirestore = mockk()
    private val userDao: UserDao = mockk(relaxed = true)
    private val networkChecker: NetworkChecker = mockk()
    private val context: Context = mockk(relaxed = true)

    @Before
    fun setUp() {
        authRepository = AuthRepositoryImpl(auth, firestore, userDao, networkChecker, context)
    }

    @Test
    fun `login with firebase success`() = runBlockingTest {
        // Given
        val email = "test@example.com"
        val password = "password"
        val uid = UUID.randomUUID().toString()
        val role = UserRole.USER
        val authResult: AuthResult = mockk()
        val firebaseUser: FirebaseUser = mockk()
        val documentSnapshot: DocumentSnapshot = mockk()

        coEvery { networkChecker.isConnected() } returns true
        coEvery { auth.signInWithEmailAndPassword(email, password) } returns mockk {
            coEvery { await() } returns authResult
        }
        coEvery { authResult.user } returns firebaseUser
        coEvery { firebaseUser.uid } returns uid
        coEvery { firestore.collection("users").document(uid).get() } returns mockk {
            coEvery { await() } returns documentSnapshot
        }
        coEvery { documentSnapshot.getString("role") } returns role.name

        // When
        val result = authRepository.login(email, password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(role, result.getOrNull()?.role)
    }

    @Test
    fun `login with room success`() = runBlockingTest {
        // Given
        val email = "test@example.com"
        val password = "password"
        val hashedPassword = com.example.store.util.PasswordHasher.hash(password)
        val role = UserRole.USER
        val userEntity = UserEntity(uid = "1", email = email, passwordHash = hashedPassword, role = role.name)

        coEvery { networkChecker.isConnected() } returns false
        coEvery { userDao.getUserByEmail(email) } returns userEntity

        // When
        val result = authRepository.login(email, password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(role, result.getOrNull()?.role)
    }

    @Test
    fun `register offline success`() = runBlockingTest {
        // Given
        val email = "test@example.com"
        val password = "password"
        val role = UserRole.USER

        coEvery { networkChecker.isConnected() } returns false

        // When
        val result = authRepository.register(email, password, role)

        // Then
        assertTrue(result.isSuccess)
    }
}
