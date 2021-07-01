package com.flethy.mylibrary.model.user.repository

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AuthAppRepository(private val application: Application) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    private val collectionReference = firebaseFirestore.collection("users")

    val userLiveData: MutableLiveData<FirebaseUser?> = MutableLiveData()
    private val loggedOutLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val name: MutableLiveData<String> = MutableLiveData()
    val username: MutableLiveData<String> = MutableLiveData()
    val country: MutableLiveData<String> = MutableLiveData()
    val age: MutableLiveData<String> = MutableLiveData()
    val email: MutableLiveData<String> = MutableLiveData()
    val photo: MutableLiveData<Uri> = MutableLiveData()

    suspend fun login(email: String?, password: String?) {
        withContext(Dispatchers.IO) {
            firebaseAuth.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(
                    ContextCompat.getMainExecutor(application.applicationContext),
                    { task ->
                        if (task.isSuccessful) {
                            userLiveData.postValue(firebaseAuth.currentUser)
                        } else {
                            Toast.makeText(
                                application.applicationContext,
                                "Login Failure: " + task.exception?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }
    }

    suspend fun register(email: String?, password: String?, name: String?) {
        withContext(Dispatchers.IO) {
            firebaseAuth.createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(
                    ContextCompat.getMainExecutor(application.applicationContext),
                    { task ->
                        if (task.isSuccessful) {
                            userLiveData.postValue(firebaseAuth.currentUser)
                            saveUserData(
                                email = email,
                                name = name!!,
                                age = "",
                                country = "",
                                username = ""
                            )
                        } else {
                            Toast.makeText(
                                application.applicationContext,
                                "Registration Failure: " + task.exception?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }
    }

    fun saveUserData(name: String, username: String, email: String, country: String, age: String) {
        val user = HashMap<String, Any>(0)
        user["full_name"] = name
        user["email"] = email
        user["username"] = username
        user["country"] = country
        user["age"] = age
        Log.e("125", user.toString())
        collectionReference.document(firebaseAuth.currentUser!!.uid).set(user)
    }

    fun getUserData() {
        val userID = firebaseAuth.currentUser!!.uid
        val documentReference = collectionReference.document(userID)

        documentReference.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    name.postValue(document.data?.get("full_name").toString())
                    username.postValue(document.data?.get("username").toString())
                    country.postValue(document.data?.get("country").toString())
                    age.postValue(document.data?.get("age").toString())
                    email.postValue(document.data?.get("email").toString())
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    suspend fun logOut() {
        withContext(Dispatchers.IO) {
            firebaseAuth.signOut()
            loggedOutLiveData.postValue(true)
            userLiveData.postValue(null)
        }
    }

    init {
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(firebaseAuth.currentUser)
            loggedOutLiveData.postValue(false)
        }
    }
}