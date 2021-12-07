package kr.kyunghee.torquefeedback_20

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScaleViewModel: ViewModel() {
    val repository = Repository(RetrofitBuilder.apiService)
    val receiveMessage = MutableLiveData<String>()

    fun sendMessage(message: Message){
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.sendMessage(message)
            response.let {
                receiveMessage.postValue(it!!.result)
            }
        }
    }

    fun cancel(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.sendCancel()
            response.let {
                receiveMessage.postValue(it!!.result)
            }
        }
    }
}