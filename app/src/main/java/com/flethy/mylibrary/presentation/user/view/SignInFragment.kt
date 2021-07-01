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


class SignInFragment(private val onMainClickListener: OnMainListener) : Fragment() {

    private var emailEt: EditText? = null
    private var passwordEt: EditText? = null

    private var redirectSignUpButton: TextView? = null

    private var loginRegisterViewModel: LoginRegisterViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginRegisterViewModel = ViewModelProviders.of(this).get(
            LoginRegisterViewModel::class.java
        )
        loginRegisterViewModel!!.userLiveData.observe(this,
            { firebaseUser ->
                if (firebaseUser != null) {
                    fragmentManager?.let {
                        onMainClickListener.onMainClick()
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
    ): View? = inflater.inflate(R.layout.sign_in_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViews(parent = view)
    }

    private fun findViews(parent: View) {
        emailEt = parent.findViewById(R.id.email_input)
        passwordEt = parent.findViewById(R.id.password_input)

        emailEt?.doAfterTextChanged {
            if (it.isNullOrEmpty()) emailEt?.setBackgroundResource(R.drawable.edit_text_wrong)
            else emailEt?.setBackgroundResource(R.drawable.edit_text_right)
        }
        passwordEt?.doAfterTextChanged {
            if (it.isNullOrEmpty()) passwordEt?.setBackgroundResource(R.drawable.edit_text_wrong)
            else passwordEt?.setBackgroundResource(R.drawable.edit_text_right)
        }

        val signInButton = parent.findViewById<Button>(R.id.sign_in_btn)
        signInButton.setOnClickListener { signIn() }

        redirectSignUpButton = parent.findViewById(R.id.redirect_to_sign_up)
        redirectSignUpButton?.setOnClickListener { redirectToSignUp() }
    }

    private fun signIn() {
        val email: String = emailEt?.text.toString()
        val password: String = passwordEt?.text.toString().trim()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            loginRegisterViewModel!!.login(email, password)
        } else {
            Toast.makeText(
                context,
                "Email Address and Password Must Be Entered",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun redirectToSignUp() {
        fragmentManager?.let {
            it.beginTransaction()
                .replace(R.id.container, SignUpFragment.newInstance(onMainClickListener))
                .commit()
        }
    }

    companion object {
        fun newInstance(onMainClickListener: OnMainListener) = SignInFragment(onMainClickListener)
    }

}