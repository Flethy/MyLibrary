package com.flethy.mylibrary.model.booklist.network

import com.google.gson.annotations.SerializedName

class SearchBookResponse(

    @SerializedName("page")
    val page: Int,

    @SerializedName("items")
    val books: List<BookNetworkModel>

)