package com.flethy.mylibrary.model.booklist.entities

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.flethy.mylibrary.model.booklist.repositories.StringListConverter
import com.flethy.mylibrary.model.review.entities.Review
import org.jetbrains.annotations.NotNull
import retrofit2.http.Url
import java.io.InputStream
import java.io.Serializable
import java.net.URL

@Entity
@TypeConverters(StringListConverter::class)
data class Book(
    @PrimaryKey
    @NotNull
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val authors: List<String> = emptyList(),
    val publisher: String = "",
    val publishedDate: String = "",
    val description: String = "",
    val pageCount: Int = 0,
    val thumbnail: String = "",
    var isLike: Boolean = false,
    var rating: Double = 0.0,
    var review_count: Long = 0,
    val reviews: ArrayList<Review> = arrayListOf()
    ) : Serializable