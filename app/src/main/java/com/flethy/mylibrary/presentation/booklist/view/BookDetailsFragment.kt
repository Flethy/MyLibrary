package com.flethy.mylibrary.presentation.booklist.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import coil.load
import com.flethy.mylibrary.R
import com.flethy.mylibrary.model.booklist.entities.Book
import com.flethy.mylibrary.presentation.review.view.CreateReviewFragment
import com.flethy.mylibrary.presentation.review.view.CreateReviewFragment.Companion.BOOK_TO_REVIEW_KEY
import com.flethy.mylibrary.presentation.review.view.CreateReviewFragment.Companion.RATING_TO_REVIEW_KEY
import com.google.android.material.snackbar.Snackbar


class BookDetailsFragment : Fragment() {

    private var backButton: ImageView? = null
    private var addReviewButton: Button? = null
    private var ratingBar: RatingBar? = null

    private var book: Book? = null

    companion object {
        fun newInstance() = BookDetailsFragment()
        const val BOOK_KEY = "book"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.book_details_fragment, container, false)

        findViews(view)
        setListeners()

        return view
    }

    @SuppressLint("ResourceAsColor")
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

    private fun findViews(view: View?) {
        backButton = view?.findViewById(R.id.back)
        addReviewButton = view?.findViewById(R.id.btn_add_review)
        ratingBar = view?.findViewById(R.id.rating_bar)
    }

    private fun setListeners() {
        backButton?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
        addReviewButton?.setOnClickListener {
            redirectToCreateReview()
        }
    }

    private fun redirectToCreateReview() {
        val createReview = CreateReviewFragment.newInstance()
        val ratingFromUser: Double = ratingBar?.rating?.toDouble() ?: 0.0
        val bundle = Bundle()
        bundle.putSerializable(BOOK_TO_REVIEW_KEY, book)
        bundle.putDouble(RATING_TO_REVIEW_KEY, ratingFromUser)
        createReview.arguments = bundle
        fragmentManager?.let {
            it.beginTransaction()
                .addToBackStack(null)
                .add(R.id.container, createReview)
                .commit()
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
        view.findViewById<TextView>(R.id.book_description).text = currentBook?.description
    }

}