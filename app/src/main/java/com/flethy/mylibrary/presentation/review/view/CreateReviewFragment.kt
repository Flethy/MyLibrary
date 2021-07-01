package com.flethy.mylibrary.presentation.review.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import coil.load
import com.flethy.mylibrary.R
import com.flethy.mylibrary.model.booklist.entities.Book
import com.flethy.mylibrary.model.review.entities.Review
import com.flethy.mylibrary.presentation.booklist.view.BookDetailsFragment
import com.flethy.mylibrary.presentation.booklist.view.BookListFragment
import com.flethy.mylibrary.presentation.booklist.viewmodel.BooksViewModel
import com.flethy.mylibrary.presentation.review.viewmodel.ReviewViewModel
import com.flethy.mylibrary.presentation.user.viewmodel.LoginRegisterViewModel
import com.google.android.material.snackbar.Snackbar

class CreateReviewFragment : Fragment() {

    private var backButton: ImageView? = null
    private var saveButton: Button? = null
    private var textReview: EditText? = null

    private var book: Book? = null
    private var rating: Double = 0.0

    private var reviewViewModel: ReviewViewModel? = null


    companion object {
        fun newInstance() = CreateReviewFragment()
        const val BOOK_TO_REVIEW_KEY = "book_to_review"
        const val RATING_TO_REVIEW_KEY = "rating_to_review"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_review_fragment, container, false)

        findViews(view)
        setListeners()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reviewViewModel = ViewModelProviders.of(this).get(
            ReviewViewModel::class.java
        )

        arguments?.let {
            book = it.getSerializable(BOOK_TO_REVIEW_KEY) as Book?
            rating = it.getDouble(RATING_TO_REVIEW_KEY)
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
    }

    private fun findViews(view: View?) {
        backButton = view?.findViewById(R.id.add_review_back)
        saveButton = view?.findViewById(R.id.btn_save_review)
        textReview = view?.findViewById(R.id.edit_text_review)
    }

    private fun setListeners() {
        backButton?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
        saveButton?.setOnClickListener {
            saveReview()
        }
    }

    private fun saveReview() {
        val text = textReview?.text.toString()
        val review = Review(id = "", bookId = book!!.id, userId = "", reviewText = text, rating = rating, likes = 0)
        reviewViewModel?.setReview(review, book!!)
        Snackbar.make(requireView(), R.string.review_saved, Snackbar.LENGTH_SHORT).show()
        fragmentManager?.popBackStack()
    }

}