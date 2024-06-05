package kr.nbc.momo.presentation.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    fun setUser(user: User) {
        _user.value = user
    }

    fun updateUser(updates: (User) -> User) {
        _user.value = _user.value?.let(updates)
    }
}

data class User(
    val isFirstLaunch: Boolean = true,
    val isLoggedIn: Boolean = false,
    val termsAccepted: Boolean = false
)