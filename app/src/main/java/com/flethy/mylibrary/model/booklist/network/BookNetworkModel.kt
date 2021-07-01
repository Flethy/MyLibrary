package com.flethy.mylibrary.model.booklist.network

import android.graphics.Bitmap
import android.net.Uri
import com.google.gson.annotations.SerializedName
import retrofit2.http.Url
import java.io.InputStream
import java.net.URL

data class BookNetworkModel(

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("volumeInfo")
    var volumeInfo: VolumeInfo

)

class VolumeInfo(

    @SerializedName("title")
    var title: String,

    @SerializedName("subtitle")
    var subtitle: String,

    @SerializedName("authors")
    var authors: List<String>,

    @SerializedName("publisher")
    var publisher: String,

    @SerializedName("publishedDate")
    var publishedDate: String,

    @SerializedName("description")
    var description: String,

    @SerializedName("pageCount")
    var pageCount: Int,

    @SerializedName("imageLinks")
    var imageLinks: Cover?

)

class Cover(
    @SerializedName("thumbnail")
    var thumbnail: String?
)