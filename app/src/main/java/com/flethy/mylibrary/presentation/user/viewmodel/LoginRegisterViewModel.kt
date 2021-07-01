package com.flethy.mylibrary.presentation.user.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.flethy.mylibrary.R
import com.flethy.mylibrary.model.user.repository.AuthAppRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch


class LoginRegisterViewModel(application: Application) :
    AndroidViewModel(application) {
    private val authAppRepository: AuthAppRepository = AuthAppRepository(application)
    val userLiveData: MutableLiveData<FirebaseUser?> = authAppRepository.userLiveData

    val name: MutableLiveData<String> = authAppRepository.name
    val username: MutableLiveData<String> = authAppRepository.username
    val country: MutableLiveData<String> = authAppRepository.country
    val age: MutableLiveData<String> = authAppRepository.age
    val email: MutableLiveData<String> = authAppRepository.email
    val photo: MutableLiveData<Uri> = authAppRepository.photo

    fun login(email: String?, password: String?) {
        viewModelScope.launch {
            authAppRepository.login(email, password)
        }
    }

    fun register(email: String?, password: String?, name: String?) {
        viewModelScope.launch {
            authAppRepository.register(email, password, name)
        }
    }

    fun logOut() {
        viewModelScope.launch {
            authAppRepository.logOut()
        }
    }

    fun getUserData() {
        authAppRepository.getUserData()
    }

    fun saveUserData(name: String, username: String, email: String = this.email.value.toString(), country: String, age: String) {

//        "android.resource://$packageName/${R.drawable.user}"
        authAppRepository.saveUserData(name = name, username = username, email = email, country = country, age = age)
    }

}