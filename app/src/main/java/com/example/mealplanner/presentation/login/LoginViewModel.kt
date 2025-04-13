package com.example.mealplanner.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.common.Resource
import com.example.mealplanner.data.repository.LoginRepositoryImpl
import com.example.mealplanner.domain.usecase.LoginUseCase
import com.example.mealplanner.domain.usecase.SaveTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val saveTokenUseCase: SaveTokenUseCase
) : ViewModel() {
    private val _loginInputState = MutableStateFlow(LoginInput())
    val loginInputState = _loginInputState.asStateFlow()

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    fun updatePasswordText(
        password: String
    ) {
        _loginInputState.update { state -> state.copy(password = password) }
    }

    fun updateUsernameText(username: String) {
        _loginInputState.update { state -> state.copy(username = username) }

    }

    fun login() {
        loginUseCase(
            _loginInputState.value.username,
            _loginInputState.value.password
        ).onEach { result ->
            _loginState.value = when (result) {
                is Resource.Loading -> LoginState(isLoading = true)
                is Resource.Success -> {
                    withContext(Dispatchers.IO) {
                        saveTokenUseCase(result.data)
                        LoginState(token = result.data)
                    }
                }

                is Resource.Error -> LoginState(error = result.message)
            }
        }.launchIn(viewModelScope)
    }
}