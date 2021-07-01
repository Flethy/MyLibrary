package com.flethy.mylibrary.presentation.booklist.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.flethy.mylibrary.model.booklist.entities.Book
import com.flethy.mylibrary.model.booklist.repositories.BooksRepository
import com.flethy.mylibrary.model.review.ReviewRepository
import com.flethy.mylibrary.presentation.booklist.Navigator
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class BooksViewModel(val booksRepository: BooksRepository, val navigator: Navigator) : ViewModel() {

//    val myBookList: LiveData<ArrayList<Book>> get() = _myBookList

//    val currentBook: LiveData<Book> get() = _currentBook
//    private val _currentBook: MutableLiveData<Book> = MutableLiveData()

    val myBookList: LiveData<ArrayList<Book>> get() = booksRepository._myBookList

    val _currentBook: MutableLiveData<Book?>
        get() = booksRepository._currentBook

    @ExperimentalCoroutinesApi
    val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

    private val _searchState = MutableLiveData<SearchState>()

    fun getFavouriteBooks(): ArrayList<Book> {
        return booksRepository.getFavouriteBooks()
    }

//    fun searchBook(query: List<String>) {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                _myBookList.postValue(booksRepository.searchBook(query))
//            }
//        }
//    }

    fun isBookFav(book: Book) {
        booksRepository.isBookFav(book)
    }

    suspend fun searchCurrentBook(bookId: String): Book? {
        var book: Book? = null
        val job = viewModelScope.launch(Dispatchers.IO) {
                book = booksRepository.searchCurrentBook(bookId)
        }
        job.join()
        return book
    }

    fun saveBookToFav(book: Book) {
        viewModelScope.launch {
            booksRepository.saveBookToFav(book)
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private val _searchResult = queryChannel
        .asFlow()
        .debounce(500)
        .onEach {
            _searchState.value = Loading
        }
        .mapLatest {
            if (it.isEmpty()) {
                EmptyQuery
            } else {
                try {
                    val result = booksRepository.searchBooks(it)
                    if (result.isEmpty()) {
                        EmptyResult
                    } else {
                        ValidResult(result)
                    }
                } catch (e: Throwable) {
                    if (e is CancellationException) {
                        throw e
                    } else {
                        Log.w(BooksViewModel::class.java.name, e)
                        ErrorResult(e)
                    }
                }
            }
        }
        .onEach {
            _searchState.value = Ready
        }
//        .catch { emit(TerminalError) }
        .asLiveData(viewModelScope.coroutineContext)


    @ExperimentalCoroutinesApi
    @FlowPreview
    val searchResult: LiveData<BooksResult>
        get() = _searchResult

    val searchState: LiveData<SearchState>
        get() = _searchState

    fun onBookAction(it: Book) {
        navigator.navigateTo("https://www.themoviedb.org/movie/${it.id}")
    }

    fun onBookLike(it: Book) {
        
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val repo: BooksRepository, private val navigator: Navigator) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BooksViewModel(booksRepository = repo, navigator = navigator) as T
        }
    }

}