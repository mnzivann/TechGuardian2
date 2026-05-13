package com.example.techguardian2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techguardian2.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    fun performLogin(username: String, password: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            // Simulamos que el servidor nos dio este token JWT
            val fakeToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

            // Lo guardamos bajo llave en DataStore
            tokenManager.saveToken(fakeToken)

            // Avisamos que el proceso terminó para navegar
            onComplete()
        }
    }
}