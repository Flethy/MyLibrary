package com.flethy.mylibrary.presentation.review.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import coil.load
import com.flethy.mylibrary.R
import com.flethy.mylibrary.model.booklist.entities.Book
import com.flethy.mylibrary.model.review.entities.Review
import com.flethy.mylibrary.presentation.booklist.view.BookDetailsFragment
import com.flethy.mylibrary.presentation.user.view.UserFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage

class ReviewDetailsFragment : Fragment() {

    private var backButton: ImageView? = null
    private var accountImage: ImageView? = null

    private var review: Review? = null

    companion object {
        fun newInstance() = ReviewDetailsFragment()
        const val REVIEW_KEY = "review"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.review_details_fragment, container, false)

        findViews(view)
        setListeners()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            review = it.getSerializable(REVIEW_KEY) as Review?
            Log.e("review", review.toString())
        }
        if (review == null) {
            Snackbar.make(view, "Loading error", Snackbar.LENGTH_SHORT).show()
        } else {
            insertReviewData(view)
        }
    }

    private fun insertReviewData(view: View) {

        val firebaseFirestore = FirebaseFirestore.getInstance()
        val booksReference = firebaseFirestore.collection("all_books")

        booksReference.get().addOnSuccessListener { result ->
            for (document in result) {
                if (document.id == review?.bookId) {
                    val book: Book = document.toObject()

                    view.findViewById<ImageView>(R.id.book_cover).load(book.thumbnail)
                    view.findViewById<TextView>(R.id.book_title).text = book.title
                    if (book?.review_count == 0.toLong()) view.findViewById<TextView>(R.id.book_rating).text = "0"
                    else view.findViewById<TextView>(R.id.book_rating).text = String.format("%.2f", (book?.rating?.div(book?.review_count)))
                    view.findViewById<TextView>(R.id.book_review_count).text = getString(R.string.count_reviews, book?.review_count)

                    if (book?.authors?.joinToString(separator = ", ", prefix = "", postfix = "").toString() == "null") {
                        view.findViewById<TextView>(R.id.book_author).text = ""
                    } else {
                        view.findViewById<TextView>(R.id.book_author).text = book?.authors?.joinToString(separator = ", ", prefix = "", postfix = "").toString()
                    }

                    break
                }
            }
        }

        val usersReference = firebaseFirestore.collection("users")
        usersReference.get().addOnSuccessListener { result ->
            for (document in result) {
                if (document.id == review?.userId) {
                    view.findViewById<TextView>(R.id.account_name).text = document.data["full_name"].toString()
                    break
                }
            }
        }
        val storageReference = FirebaseStorage.getInstance().reference
        val profileRef = storageReference.child("profileImages").child("${review?.userId}.jpg")
        profileRef.downloadUrl.addOnSuccessListener { uri: Uri ->
            accountImage?.load(uri)
        }

        view.findViewById<RatingBar>(R.id.rating_bar).rating = review?.rating?.toFloat() ?: 0f
        view.findViewById<TextView>(R.id.review_text).text = review?.reviewText

        view.findViewById<TextView>(R.id.review_rating).text = review?.likes.toString()
    }

    private fun findViews(view: View?) {
        backButton = view?.findViewById(R.id.back)
        accountImage = view?.findViewById(R.id.account_image)
    }

    private fun setListeners() {
        backButton?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
        accountImage?.setOnClickListener {
            redirectToUserFragment()
        }
    }

    private fun redirectToUserFragment() {
        val userFragment = UserFragment.newInstance()
        val bundle = Bundle()
        bundle.putString(UserFragment.USER_KEY, review?.userId)
        userFragment.arguments = bundle
        fragmentManager?.let {
            it.beginTransaction()
                .addToBackStack(null)
                .add(R.id.container, userFragment)
                .commit()
        }
    }

}