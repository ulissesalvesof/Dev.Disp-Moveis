package com.example.planner.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
): ViewModel() {
    var loginResult: ((Boolean) -> Unit) ?= null
    var registerResult: ((Boolean) -> Unit) ?= null
    fun isUserAuthenticated(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun login (
        email: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.loginUser(email, password)
            onResult(success)
        }
    }

    fun resetPassword (
        email: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.resetPassword(email)
            onResult(success)
        }
    }

    fun getUserName(onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val name = repository.getUserName()
            onResult(name)
        }
    }

    fun loginWithGoogle(idToken: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.loginWithGoogle(idToken)
            onResult(success)
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        return repository.getGoogleSignInClient(context)
    }

    fun logout() {
        repository.logout()
    }


    fun register(email: String, password: String, name: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.registerUser(email, password, name)
            onResult(success)
        }
    }
}