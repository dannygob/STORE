package com.example.store.presentation.login.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.store.domain.model.LoginResult
import com.example.store.domain.model.UserRole
import com.example.store.domain.repository.AuthRepository
import com.example.store.domain.usecase.LoginUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: LoginViewModel
    private val loginUseCase: LoginUseCase = mockk()
    private val authRepository: AuthRepository = mockk()
    private val application: Application = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(loginUseCase, authRepository, application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `onEvent Register should set registrationSuccess to true when registration is successful`() = runBlockingTest {
        // Given
        val email = "test@example.com"
        val password = "password"
        val role = UserRole.USER
        coEvery { authRepository.register(email, password, role) } returns Result.success(LoginResult(role))

        // When
        viewModel.onEvent(LoginEvent.Register(email, password, role))

        // Then
        assertTrue(viewModel.uiState.value.registrationSuccess)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals("Usuario registrado correctamente.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onEvent Register should set error message when registration fails`() = runBlockingTest {
        // Given
        val email = "test@example.com"
        val password = "password"
        val role = UserRole.USER
        val errorMessage = "Registration failed"
        coEvery { authRepository.register(email, password, role) } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.onEvent(LoginEvent.Register(email, password, role))

        // Then
        assertEquals(false, viewModel.uiState.value.registrationSuccess)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(errorMessage, viewModel.uiState.value.errorMessage)
    }
}
