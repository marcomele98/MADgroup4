package it.polito.madgroup4.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


sealed class Status {
    object Loading : Status()

    object Running: Status()
    data class Error(val message: String, val nextRoute: String?) : Status()
    data class Success(val message: String, val nextRoute: String?) : Status()
}

class LoadingStateViewModel : ViewModel() {
    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    fun setStatus(status: Status) {
        _status.value = status
    }
}

