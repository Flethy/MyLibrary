package com.flethy.mylibrary.presentation.user.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.flethy.mylibrary.OnMainListener
import com.flethy.mylibrary.R
import com.flethy.mylibrary.presentation.booklist.view.BookListFragment
import com.flethy.mylibrary.presentation.user.viewmodel.LoginRegisterViewModel


class SignUpFragment(private val onMainClickListener: OnMainListener) : Fragment() {

    private var nameEt: EditText? = null
    private var emailEt: EditText? = null
    private var passwordEt: EditText? = null
    private var repeatPasswordEt: EditText? = null

    private var redirectSignInButton: TextView? = null

    private var loginRegisterViewModel: LoginRegisterViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginRegisterViewModel = ViewModelProviders.of(this).get(
            LoginRegisterViewModel::class.java
        )
        loginRegisterViewModel!!.userLiveData.observe(this,
            { firebaseUser ->
                if (firebaseUser != null) {
                    onMainClickListener.onMainClick()
                    fragmentManager?.let {
                        it.beginTransaction()
                            .replace(R.id.container, BookListFragment.newInstance())
                            .commit()
                    }
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.sign_up_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViews(parent = view)
    }

    private fun findViews(parent: View) {
        nameEt = parent.findViewById(R.id.name_input)
        emailEt = parent.findViewById(R.id.email_input)
        passwordEt = parent.findViewById(R.id.password_input)
        repeatPasswordEt = parent.findViewById(R.id.repeat_password_input)

        nameEt?.doAfterTextChanged {
            if (it.isNullOrEmpty()) nameEt?.setBackgroundResource(R.drawable.edit_text_wrong)
            else nameEt?.setBackgroundResource(R.drawable.edit_text_right)
        }
        emailEt?.doAfterTextChanged {
            if (it.isNullOrEmpty()) emailEt?.setBackgroundResource(R.drawable.edit_text_wrong)
            else emailEt?.setBackgroundResource(R.drawable.edit_text_right)
        }
        passwordEt?.doAfterTextChanged {
            if (it.isNullOrEmpty()) passwordEt?.setBackgroundResource(R.drawable.edit_text_wrong)
            else passwordEt?.setBackgroundResource(R.drawable.edit_text_right)
        }
        repeatPasswordEt?.doAfterTextChanged {
            if (it.isNullOrEmpty() || it.toString() != passwordEt?.text.toString()) repeatPasswordEt?.setBackgroundResource(R.drawable.edit_text_wrong)
            else repeatPasswordEt?.setBackgroundResource(R.drawable.edit_text_right)
        }

        val signUpButton = parent.findViewById<Button>(R.id.sign_up_btn)
        signUpButton.setOnClickListener { signUp() }

        redirectSignInButton = parent.findViewById(R.id.redirect_to_login)
        redirectSignInButton?.setOnClickListener { redirectToSignIn() }
    }

    private fun signUp() {
        val email: String = emailEt?.text.toString()
        val password: String = passwordEt?.text.toString().trim()
        val name: String = nameEt?.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && passwordEt?.text.toString() == repeatPasswordEt?.text.toString()) {
            loginRegisterViewModel!!.register(email, password, name)
        } else {
            Toast.makeText(
                context,
                "Email Address and Password Must Be Entered",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun redirectToSignIn() {
        fragmentManager?.let {
            it.beginTransaction()
                .replace(R.id.container, SignInFragment.newInstance(onMainClickListener))
                .commit()
        }
    }

    companion object {
        fun newInstance(onMainClickListener: OnMainListener) = SignUpFragment(onMainClickListener)
    }

}