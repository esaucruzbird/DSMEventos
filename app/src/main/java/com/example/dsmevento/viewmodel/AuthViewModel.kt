package com.example.dsmevento.viewmodel

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.dsmevento.data.model.User
import com.example.dsmevento.data.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    var currentUser by mutableStateOf<User?>(null)
        private set

    var loading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadCurrentUser() {
        repository.loadCurrentUserProfile { user ->
            currentUser = user
        }
    }

    fun register(
        email: String,
        password: String,
        displayName: String,
        onSuccess: () -> Unit
    ) {
        loading = true
        errorMessage = null

        repository.registerUser(
            email = email,
            password = password,
            displayName = displayName,
            onSuccess = {
                loading = false
                loadCurrentUser()
                onSuccess()
            },
            onError = {
                loading = false
                errorMessage = it
            }
        )
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        loading = true
        errorMessage = null

        repository.login(
            email = email,
            password = password,
            onSuccess = {
                loading = false
                loadCurrentUser()
                onSuccess()
            },
            onError = {
                loading = false
                errorMessage = it
            }
        )
    }

    fun loginWithGithub(
        activity: Activity,
        onSuccess: () -> Unit
    ) {
        loading = true
        errorMessage = null

        repository.loginWithGithub(
            activity = activity,
            onSuccess = {
                loading = false
                loadCurrentUser()
                onSuccess()
            },
            onError = {
                loading = false
                errorMessage = it
            }
        )
    }

    fun logout() {
        repository.logout()
        currentUser = null
    }

    fun clearError() {
        errorMessage = null
    }
}