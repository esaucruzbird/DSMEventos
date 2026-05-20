package com.example.dsmevento.data.repository

import android.app.Activity
import com.example.dsmevento.data.model.User
import com.example.dsmevento.data.remote.firebase.FirebaseAuthDataSource

class AuthRepository {

    private val dataSource = FirebaseAuthDataSource()

    fun registerUser(
        email: String,
        password: String,
        displayName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = dataSource.registerUser(email, password, displayName, onSuccess, onError)

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = dataSource.login(email, password, onSuccess, onError)

    fun loginWithGithub(
        activity: Activity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = dataSource.loginWithGithub(activity, onSuccess, onError)

    fun logout() = dataSource.logout()

    fun currentUid(): String? = dataSource.currentUid()

    fun loadCurrentUserProfile(onResult: (User?) -> Unit) {
        dataSource.loadCurrentUserProfile(onResult)
    }
}