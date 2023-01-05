package com.example.torquefeddback

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewtonViewModel:ViewModel() {
    var progress = MutableLiveData<String>("0.1")
    var step = MutableLiveData<Double>(0.1)
    private val repository = Repository(RetrofitBuilder.apiService)
    val receiveMessage = MutableLiveData<String>()

    fun sendForce(message: Force){
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.sendForce(message)
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