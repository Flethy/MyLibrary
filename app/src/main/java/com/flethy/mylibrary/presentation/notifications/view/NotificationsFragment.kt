package com.flethy.mylibrary.presentation.notifications.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flethy.mylibrary.R
import com.flethy.mylibrary.presentation.booklist.view.BookListFragment

class NotificationsFragment : Fragment() {

    companion object {
        fun newInstance() = NotificationsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.notifications_fragment, container, false)

}