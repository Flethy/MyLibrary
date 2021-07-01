package com.flethy.mylibrary.presentation.user.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import coil.load
import com.flethy.mylibrary.OnMainListener
import com.flethy.mylibrary.R
import com.flethy.mylibrary.presentation.user.viewmodel.LoginRegisterViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AccountFragment(private val onMainClickListener: OnMainListener) : Fragment() {

    private var backButton: ImageView? = null
    private var logOutButton: TextView? = null
    private var saveButton: Button? = null
    private var loginRegisterViewModel: LoginRegisterViewModel? = null
    private var nameET: EditText? = null
    private var usernameET: EditText? = null
    private var countryET: EditText? = null
    private var ageET: EditText? = null
    private var emailTV: TextView? = null
    private var photoIV: ImageView? = null
    private var searchBackground: ImageView? = null
    private var searchProgress: ProgressBar? = null

    private val storageReference = FirebaseStorage.getInstance().reference

    companion object {
        fun newInstance(onMainClickListener: OnMainListener) = AccountFragment(onMainClickListener)
        private const val TAKE_IMAGE_CODE = 188
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.account_fragment, container, false)

        findViews(view)
        setListeners()
        getUserInfo()

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginRegisterViewModel = ViewModelProviders.of(this).get(
            LoginRegisterViewModel::class.java
        )
        setObservers()
    }

    private fun findViews(view: View?) {
        backButton = view?.findViewById(R.id.acc_back)
        logOutButton = view?.findViewById(R.id.log_out)
        saveButton = view?.findViewById(R.id.save_btn)
        nameET = view?.findViewById(R.id.name_input)
        usernameET = view?.findViewById(R.id.username_input)
        countryET = view?.findViewById(R.id.country_input)
        ageET = view?.findViewById(R.id.age_input)
        emailTV = view?.findViewById(R.id.email_tv)
        photoIV = view?.findViewById(R.id.account_image)
        searchBackground = view?.findViewById(R.id.search_background)
        searchProgress = view?.findViewById(R.id.search_progress)
    }

    private fun setListeners() {
        backButton?.setOnClickListener {
            onMainClickListener.onMainClick()
            returnBack()
        }
        logOutButton?.setOnClickListener {
            Toast.makeText(context, "You logged out", Toast.LENGTH_SHORT).show()
            logOut()
        }
        saveButton?.setOnClickListener {
            saveUserInfo()
        }
        photoIV?.setOnClickListener {
            loadCustomImage()
        }
    }

    private fun loadCustomImage() {
        val openGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(openGalleryIntent, TAKE_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = data?.data
                uploadImageToFirebase(imageUri!!)
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val uid = FirebaseAuth.getInstance().uid
        val fileRef: StorageReference = storageReference.child("profileImages").child("${uid}.jpg")
        fileRef.putFile(imageUri)
            .addOnSuccessListener {
                Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show()
                fileRef.downloadUrl.addOnSuccessListener { uri: Uri -> photoIV?.load(uri) }
            }
            .addOnFailureListener { Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show() }
    }

    private fun setObservers() {
        loginRegisterViewModel?.name?.observe(this, { name ->
            nameET?.setText(name.toString())
        })
        loginRegisterViewModel?.username?.observe(this, { username ->
            usernameET?.setText(username.toString())
        })
        loginRegisterViewModel?.country?.observe(this, { country ->
            countryET?.setText(country.toString())
        })
        loginRegisterViewModel?.age?.observe(this, { age ->
            ageET?.setText(age.toString())
        })
        loginRegisterViewModel?.email?.observe(this, { email ->
            emailTV?.text = email.toString()
        })
    }

    private fun getUserInfo() {
        searchBackground?.isVisible = true
        searchProgress?.isVisible = true

        loginRegisterViewModel?.getUserData()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val profileRef = storageReference.child("profileImages").child("${uid}.jpg")
        profileRef.downloadUrl.addOnSuccessListener { uri: Uri ->
            photoIV?.load(uri)
        }

        searchBackground?.isVisible = false
        searchProgress?.isVisible = false
    }

    private fun saveUserInfo() {
        loginRegisterViewModel?.saveUserData(
            name = nameET?.text.toString(),
            username = usernameET?.text.toString(),
            country = countryET?.text.toString(),
            age = ageET?.text.toString()
        )
        Snackbar.make(this.requireView(), R.string.data_saved, Snackbar.LENGTH_SHORT).show()
    }

    private fun logOut() {
        loginRegisterViewModel?.logOut()
        fragmentManager?.let {
            it.beginTransaction()
                .replace(R.id.container, SignInFragment.newInstance(onMainClickListener))
                .commit()
        }
    }

    private fun returnBack() {
        fragmentManager?.popBackStack()
    }

}