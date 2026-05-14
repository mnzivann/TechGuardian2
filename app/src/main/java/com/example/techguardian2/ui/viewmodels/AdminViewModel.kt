package com.example.techguardian2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techguardian2.data.remote.ApiService
import com.example.techguardian2.data.remote.CreateUserDto
import com.example.techguardian2.data.remote.UserDto
import com.example.techguardian2.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _usersList = MutableStateFlow<List<UserDto>>(emptyList())
    val usersList: StateFlow<List<UserDto>> = _usersList

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            try {
                val token = tokenManager.token.first() ?: ""
                _usersList.value = apiService.getUsers(token)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun saveUser(id: Int?, fullName: String, username: String, pass: String, role: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val token = tokenManager.token.first() ?: ""
                val request = CreateUserDto(fullName, username, pass, role)

                val response = if (id == null) {
                    apiService.createUser(token, request)
                } else {
                    apiService.updateUser(token, id, request)
                }

                if (response.success) {
                    loadUsers() // Recargar la lista
                    onSuccess()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            try {
                val token = tokenManager.token.first() ?: ""
                val response = apiService.deleteUser(token, id)
                if (response.success) {
                    loadUsers() // Recargar lista tras borrar
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}