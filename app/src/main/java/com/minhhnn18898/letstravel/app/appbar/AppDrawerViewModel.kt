package com.minhhnn18898.letstravel.app.appbar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.account.data.model.UserInfo
import com.minhhnn18898.account.usecase.GetAuthUserInfoUseCase
import com.minhhnn18898.account.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppDrawerViewModel @Inject constructor(
    private val getAuthStateUseCase: GetAuthUserInfoUseCase,
    private val signOutUseCase: SignOutUseCase
): ViewModel() {

    var userInfo: UserInfo? by mutableStateOf(null)

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            getAuthStateUseCase.execute(Unit)?.collect {
                userInfo = it
            }
        }
    }

    fun onClickSignOut() {
        signOutUseCase.execute(Unit)
    }
}