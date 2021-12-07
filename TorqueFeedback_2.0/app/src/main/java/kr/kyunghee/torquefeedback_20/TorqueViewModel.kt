package kr.kyunghee.torquefeedback_20

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TorqueViewModel:ViewModel() {
    var torque = MutableLiveData<Double>(3.1)
    private val repository = Repository(RetrofitBuilder.apiService)
    val receiveMessage = MutableLiveData<String>()

    fun sendTorque(message: Torque){
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.sendTorque(message)
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