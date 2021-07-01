package com.flethy.mylibrary.model.booklist.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BooksApi {

    @GET("v1/volumes")
    suspend fun searchBook(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("page") page: Int = 1
    ): SearchBookResponse

    @GET("v1/volumes/{id}")
    suspend fun searchCurrentBook(
        @Path("id") id: String,
        @Query("api_key") apiKey: String
    ): BookNetworkModel

}