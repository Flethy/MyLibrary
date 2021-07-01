package com.flethy.mylibrary.presentation.timeline.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import coil.load
import com.flethy.mylibrary.OnMainListener
import com.flethy.mylibrary.R
import com.flethy.mylibrary.model.booklist.entities.Book
import com.flethy.mylibrary.presentation.booklist.view.BookDetailsFragment
import com.flethy.mylibrary.presentation.user.view.AccountFragment
import com.google.android.material.snackbar.Snackbar

class TimelineDetailsFragment: Fragment() {

    private var backButton: ImageView? = null

    private var book: Book? = null

    companion object {
        fun newInstance() = TimelineDetailsFragment()
        const val BOOK_KEY = "book"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.timeline_details_fragment, container, false)

        findViews(view)
        setListeners()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            book = it.getSerializable(BOOK_KEY) as Book?
        }
        if (book == null) {
            Snackbar.make(view, "Loading error", Snackbar.LENGTH_SHORT).show()
        } else {
            insertBookData(view)
        }
    }

    private fun insertBookData(view: View) {
        val currentBook = book
        view.findViewById<ImageView>(R.id.book_cover).load(currentBook?.thumbnail)
        view.findViewById<TextView>(R.id.book_title).text = currentBook?.title
        view.findViewById<TextView>(R.id.book_author).text = currentBook?.authors?.joinToString(separator = ", ", prefix = "", postfix = "")
        if (currentBook?.review_count == 0.toLong()) view.findViewById<TextView>(R.id.book_rating).text = "0"
        else view.findViewById<TextView>(R.id.book_rating).text = String.format("%.2f", (currentBook?.rating?.div(currentBook?.review_count)))
        view.findViewById<TextView>(R.id.book_review_count).text = getString(R.string.count_reviews, currentBook?.review_count)
    }

    private fun findViews(view: View?) {
        backButton = view?.findViewById(R.id.back)
    }

    private fun setListeners() {
        backButton?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

}