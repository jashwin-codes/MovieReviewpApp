package com.example.moviereviewapp.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviereviewapp.model.LoginResponse
import com.example.moviereviewapp.model.*
import com.example.moviereviewapp.repository.LoginRepository
import com.example.moviereviewapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject

class LoginViewModel : ViewModel() {
    private val loginRepository = LoginRepository()
    val sessionId: MutableLiveData<Resource<SessionId>> = MutableLiveData()

    //    val account: MutableLiveData<Resource<Account>> = MutableLiveData()
    val loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()


    fun login(userName: String, password: String) {
        viewModelScope.launch {

            launch {
                val token =
                    withContext(Dispatchers.IO) { getRequestToken() } ?: return@launch
                val loginRequest = LoginRequest(userName, password, token.request_token)
                val response = loginRepository.login(loginRequest)
                if (response is Resource.Success) {
                    if (response.data!!.success) {
                        withContext(Dispatchers.IO) {
                            getSessionId(token, response)
                        }
                    }

                } else
                    loginResponse.postValue(response)
//                if (response.isSuccessful) {
//                    val loginResponse = response.body()
//
//                    if (loginResponse!!.success) {
//                        withContext(Dispatchers.Default) {
//                            getSessionId(
//                                token
//                            )
//                        }
//                        this@LoginViewModel.loginResponse.postValue(Resource.Success(loginResponse))
//                    }
//
//                } else if (!response.isSuccessful) {
//                    Log.d("Det", "fail: ${response.errorBody()!!.charStream()} ")
//                    this@LoginViewModel.loginResponse.postValue(Resource.Error("INVALID INPUT"))
//                }

            }
        }


    }

    suspend fun getRequestToken(): AuthToken? {

        loginResponse.postValue(Resource.Loading())
        val response = loginRepository.getAuthenticationToken()
        return if (response is Resource.Success)
            response.data
        else {
            loginResponse.postValue(
                Resource.Error((response as Resource.Error).error)
            )
            null
        }

    }

    private suspend fun getSessionId(token: AuthToken, loginResponse: Resource<LoginResponse>) {
        viewModelScope.launch {
            val response =
                loginRepository.getSessionId(createJsonRequestBody("request_token" to token.request_token))
            sessionId.postValue(response)
            if (response is Resource.Success)
                this@LoginViewModel.loginResponse.postValue(loginResponse)
            else if (response is Resource.Error)
                this@LoginViewModel.loginResponse.postValue(Resource.Error(response.error))
        }
    }

//    fun getAccount() {
//        viewModelScope.launch {
//            Repository.getAccount(sessionId.value!!.data!!.session_id)
//            account.postValue(Resource.Loading())
//            val response = Repository.getAccount(sessionId.value!!.data!!.session_id)
//            val token = response.body()
//            if (response.isSuccessful)
//                account.postValue(Resource.Success(token!!))
//            // else
//            //         account.postValue(Resource.Error(response.message()))
//        }
//    }

    private fun createJsonRequestBody(vararg params: Pair<String, String>) =
        RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(mapOf(*params)).toString()
        )

}
