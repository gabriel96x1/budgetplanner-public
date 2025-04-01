package com.bytesdrawer.authmodule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytesdrawer.authmodule.data.AuthRepository
import com.bytesdrawer.authmodule.models.ConfirmationData
import com.bytesdrawer.authmodule.models.CredentialsData
import com.bytesdrawer.authmodule.utils.AuthSharedPreferencesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesUtil: AuthSharedPreferencesUtil
) : ViewModel() {

    private val _signInStatus = MutableStateFlow<Boolean?>(null)
    val signInStatus: StateFlow<Boolean?> get() = _signInStatus

    private val _signUpStatus = MutableStateFlow<Boolean?>(null)
    val signUpStatus: StateFlow<Boolean?> get() = _signUpStatus

    private val _signUpCodeConfirmationStatus = MutableStateFlow<Boolean?>(null)
    val signUpCodeConfirmationStatus: StateFlow<Boolean?> get() = _signUpCodeConfirmationStatus

    fun signIn(data: CredentialsData) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.signIn(data)
            if (response.isSuccessful) {
                val result = response.body()?.result
                if (result != null) {
                    preferencesUtil.setAuthToken(result.idToken)
                    _signInStatus.emit(true)
                }
            } else {
                _signInStatus.emit(false)
            }
        }
    }

    fun signUp(data: CredentialsData) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.signUp(data)
            if (response.isSuccessful) {
                _signUpStatus.emit(true)
            } else {
                _signUpStatus.emit(false)
            }
        }
    }

    fun signUpCodeConfirmation(data: ConfirmationData) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.signUpCodeConfirmation(data)
            if (response.isSuccessful) {
                _signUpCodeConfirmationStatus.emit(true)
            } else {
                _signUpCodeConfirmationStatus.emit(false)
            }
        }
    }

}