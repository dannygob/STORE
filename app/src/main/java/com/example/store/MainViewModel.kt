package com.example.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {

    val currentAuthState: StateFlow<FirebaseUser?> = authRepository.getAuthState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // Or FirebaseAuth.getInstance().currentUser if immediate check is desired
        )
}
