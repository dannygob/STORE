package com.example.store.presentation.login

import app.cash.turbine.test
import com.example.store.presentation.login.viewmodel.LoginViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel()
    }

    @Test
    fun `onUsernameChange updates username in uiState`() = runTest {
        val newUsername = "testUser"
        viewModel.onUsernameChange(newUsername)
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.username).isEqualTo(newUsername)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPasswordChange updates password in uiState`() = runTest {
        val newPassword = "testPassword"
        viewModel.onPasswordChange(newPassword)
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.password).isEqualTo(newPassword)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onLoginClicked with correct credentials updates uiState to loginSuccess`() = runTest {
        viewModel.onUsernameChange("user")
        viewModel.onPasswordChange("password")

        viewModel.uiState.test {
            skipItems(2) // Skip initial state and username/password updates

            viewModel.onLoginClicked()

            var emission = awaitItem()
            assertThat(emission.isLoading).isTrue() // Start loading

            emission = awaitItem() // Loading finished
            assertThat(emission.isLoading).isFalse()
            assertThat(emission.loginSuccess).isTrue()
            assertThat(emission.loginError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onLoginClicked with incorrect credentials updates uiState with error`() = runTest {
        viewModel.onUsernameChange("wrongUser")
        viewModel.onPasswordChange("wrongPassword")

        viewModel.uiState.test {
            skipItems(2) // Skip initial state and username/password updates

            viewModel.onLoginClicked()

            var emission = awaitItem()
            assertThat(emission.isLoading).isTrue() // Start loading

            emission = awaitItem() // Loading finished
            assertThat(emission.isLoading).isFalse()
            assertThat(emission.loginSuccess).isFalse()
            assertThat(emission.loginError).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onLoginHandled resets loginSuccess and loginError`() = runTest {
        // Simulate a successful login
        viewModel.onUsernameChange("user")
        viewModel.onPasswordChange("password")
        viewModel.onLoginClicked()
        advanceUntilIdle() // Ensure login click processing is complete

        viewModel.onLoginHandled()

        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.loginSuccess).isFalse()
            assertThat(emission.loginError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
