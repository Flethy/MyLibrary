package com.flethy.mylibrary.presentation.review.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.flethy.mylibrary.model.booklist.entities.Book
import com.flethy.mylibrary.model.review.ReviewRepository
import com.flethy.mylibrary.model.review.entities.Review

class ReviewViewModel(application: Application):
    AndroidViewModel(application) {
    private val reviewRepository: ReviewRepository = ReviewRepository(application)

    fun setReview(review: Review, book: Book) {
        reviewRepository.setReview(review, book)
    }

    fun getReviews(): ArrayList<Review> {
        return reviewRepository.getReviews()
    }
}