package it.polito.madgroup4.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.madgroup4.model.Repository
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


//TODO: valutiamo... forse dal momento in cui nella preview faremo vedere la media delle reviews possiamo togliere questa classe e recuperarle direttamente da court
@HiltViewModel
class ReviewViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    fun saveReview(review: Review) = viewModelScope.launch {
        repository.saveReview(review)
    }


}