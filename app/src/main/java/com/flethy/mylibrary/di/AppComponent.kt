package com.flethy.mylibrary.di

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.flethy.mylibrary.BuildConfig
import com.flethy.mylibrary.model.booklist.network.BooksApi
import com.flethy.mylibrary.model.booklist.repositories.BooksRepository
import com.flethy.mylibrary.presentation.booklist.Navigator
import com.flethy.mylibrary.presentation.booklist.viewmodel.BooksViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AppComponent(appContext: Context) {

    private val booksRepo: BooksRepository
    private val navigator: Navigator = Navigator(appContext)

    init {
        val api = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BooksApi::class.java)

        booksRepo = BooksRepository(api)
    }

    fun getBooksViewModel(fragment: Fragment): BooksViewModel {
        return ViewModelProvider(fragment, BooksViewModel.Factory(booksRepo, navigator)).get(BooksViewModel::class.java)
    }

}