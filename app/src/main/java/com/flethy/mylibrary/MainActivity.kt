package com.flethy.mylibrary

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.flethy.mylibrary.presentation.booklist.view.BookListFragment
import com.flethy.mylibrary.presentation.notifications.view.NotificationsFragment
import com.flethy.mylibrary.presentation.review.view.ReviewFragment
import com.flethy.mylibrary.presentation.timeline.view.TimelineFragment
import com.flethy.mylibrary.presentation.user.view.AccountFragment
import com.flethy.mylibrary.presentation.user.view.SignInFragment
import com.flethy.mylibrary.presentation.user.viewmodel.LoginRegisterViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var btnAccount: ImageView? = null
    private var bottomNavigationView: BottomNavigationView? = null

    private var loginRegisterViewModel: LoginRegisterViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        findViews()
        setListeners()

        hideMainViews()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SignInFragment.newInstance(onMainClickListener))
            .commitNow()

        loginRegisterViewModel = ViewModelProviders.of(this).get(
            LoginRegisterViewModel::class.java
        )
        loginRegisterViewModel!!.userLiveData.observe(this,
            { firebaseUser ->
                if (firebaseUser != null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, BookListFragment.newInstance())
                        .commitNow()
                }
                else {
                    hideMainViews()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, SignInFragment.newInstance(onMainClickListener))
                        .commitNow()
                }
            })
    }

    override fun onDestroy() {
        deleteViews()
        super.onDestroy()
    }

    private fun findViews() {
        btnAccount = findViewById(R.id.btn_account)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
    }

    private fun deleteViews() {
        bottomNavigationView = null
    }

    private fun setListeners() {
        btnAccount?.setOnClickListener { redirectToAccSettings() }

        bottomNavigationView?.selectedItemId = R.id.my_books
        bottomNavigationView?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.my_books -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, BookListFragment.newInstance())
                        .commitNow()
                }
                R.id.timeline -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, TimelineFragment.newInstance())
                        .commitNow()
                }
                R.id.add_review -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, ReviewFragment.newInstance())
                        .commitNow()
                }
                R.id.notifications -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, NotificationsFragment.newInstance())
                        .commitNow()
                }
            }
            true
        }
    }

    private fun redirectToAccSettings() {
        hideMainViews()
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .add(R.id.container, AccountFragment.newInstance(onMainClickListener))
            .commit()
    }

    private fun hideMainViews() {
        btnAccount?.visibility = View.GONE
        bottomNavigationView?.visibility = View.GONE
    }

    private val onMainClickListener = object : OnMainListener {
        override fun onMainClick() {
            btnAccount?.visibility = View.VISIBLE
            bottomNavigationView?.visibility = View.VISIBLE
        }
    }


}