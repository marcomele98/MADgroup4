package it.polito.madgroup4.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.madgroup4.model.Repository
import it.polito.madgroup4.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    private var _user = MutableLiveData<User>().apply { value = null }
    val user: LiveData<User> = _user

    //TODO: metto l'id dell'utente loggato in preferences o lo hardcodato
    fun getUser(id: Long) {
        repository.getById(id).observeForever { user ->
            _user.value = user
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            repository.saveUser(user)
        }
    }
}