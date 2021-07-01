package com.flethy.mylibrary.model.review.entities

import java.io.Serializable

data class Review(
    var id: String = "",
    var bookId: String = "",
    var userId: String = "",
    var reviewText: String = "",
    var rating: Double = 0.0,
    var likes: Long = 0,
    var idUsersLiked: List<String> = emptyList()
) : Serializable