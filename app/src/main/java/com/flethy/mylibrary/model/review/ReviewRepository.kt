package com.flethy.mylibrary.model.review

import android.app.Application
import android.util.Log
import com.flethy.mylibrary.model.booklist.entities.Book
import com.flethy.mylibrary.model.review.entities.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ReviewRepository(private val application: Application) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    private val reviewsReference = firebaseFirestore.collection("all_reviews")
    private val booksReference = firebaseFirestore.collection("all_books")
    private val userBooksReference = firebaseFirestore.collection("user_data").document(firebaseAuth.currentUser!!.uid).collection("books")
    private val userReviewsReference = firebaseFirestore.collection("user_data").document(firebaseAuth.currentUser!!.uid).collection("reviews")

    fun setReview(review: Review, book: Book) {

        review.userId = firebaseAuth.currentUser!!.uid
        review.id = userReviewsReference.document().id

        booksReference.get().addOnSuccessListener { result ->
            for (document in result) {
                if (document.id == book.id) {
                    book.review_count = document.data["review_count"] as Long
                    book.rating = document.data["rating"] as Double
                    break
                }
            }
        }

        var isReviewExist = false
        for (i in book.reviews.indices) {
            if (review.userId == book.reviews[i].userId) {
                book.rating = book.rating - book.reviews[i].rating + review.rating
                book.reviews[i] = review
                isReviewExist = true
                break
            }
        }

        if (!isReviewExist) {
            book.review_count++
            book.rating = book.rating + review.rating
            book.reviews.add(review)
        }

        booksReference.document(book.id).set(book)


        userReviewsReference.document(book.id).set(review)
        reviewsReference.document(review.id).set(review)

        userBooksReference.get().addOnSuccessListener { result ->
            for (document in result) {
                if (document.id == book.id) {
                    userBooksReference.document(book.id).set(book)
                    break
                }
            }
        }

    }

    fun getReviews(): ArrayList<Review> {
        var list: ArrayList<Review> = arrayListOf()

        reviewsReference.get().addOnSuccessListener { result ->
            for (document in result) {
                list.add(document.toObject())
            }
        }

        return list
    }
}