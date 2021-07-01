package com.flethy.mylibrary.model.booklist.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.flethy.mylibrary.BuildConfig
import com.flethy.mylibrary.model.booklist.entities.Book
import com.flethy.mylibrary.model.booklist.network.BookNetworkModel
import com.flethy.mylibrary.model.booklist.network.BooksApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext


class BooksRepository(private val booksApi: BooksApi) {

    var list: ArrayList<String> = ArrayList(0)

    val _myBookList = MutableLiveData<ArrayList<Book>>(ArrayList(0))

    val _currentBook = MutableLiveData<Book?>(null)

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    private val userBooksReference = firebaseFirestore.collection("user_data").document(firebaseAuth.currentUser!!.uid).collection("books")

    private val databaseUserBooksRef = FirebaseDatabase.getInstance().reference.child("users").child(firebaseAuth.currentUser!!.uid).child("books")

    internal suspend fun searchBooks(query: String, page: Int = 1): List<Book> {
        return withContext(Dispatchers.IO) {
            flowOf(
                    booksApi.searchBook(BuildConfig.API_KEY, query, page)
            )
        }
            .flowOn(Dispatchers.IO)
            .onEach { Log.d(BooksRepository::class.java.name, it.books.toString()) }
            .flatMapMerge { it.books.asFlow() }
            .map {
                Book(
                    it.id,
                    it.volumeInfo?.title ?: "",
                    it.volumeInfo?.subtitle ?: "",
                    it.volumeInfo?.authors ?: emptyList(),
                    it.volumeInfo?.publisher ?: "",
                    it.volumeInfo?.publishedDate ?: "",
                    it.volumeInfo?.description ?: "",
                    it.volumeInfo?.pageCount ?: 0,
                    getPosterUrl(it)
                )
            }
            .toList()
    }

    private fun getPosterUrl(it: BookNetworkModel) =
        if (it.volumeInfo.imageLinks?.thumbnail != null && it.volumeInfo.imageLinks != null)
            "${it.volumeInfo.imageLinks!!.thumbnail!!.substring(0, 4)}s${it.volumeInfo.imageLinks!!.thumbnail!!.substring(4, it.volumeInfo.imageLinks!!.thumbnail!!.length)}"
        else ""

    fun getFavouriteBooks(): ArrayList<Book> {
        var list: ArrayList<Book> = arrayListOf()

        userBooksReference.get().addOnSuccessListener { result ->
            for (document in result) {
                list.add(document.toObject<Book>())
            }
        }

        return list
    }

    suspend fun searchCurrentBook(bookId: String): Book {
        val bookNet = booksApi.searchCurrentBook(bookId, BuildConfig.API_KEY)
        return Book(
            bookNet.id,
            bookNet.volumeInfo.title,
            bookNet.volumeInfo.subtitle,
            bookNet.volumeInfo.authors,
            bookNet.volumeInfo.publisher,
            bookNet.volumeInfo.publishedDate,
            bookNet.volumeInfo.description,
            bookNet.volumeInfo.pageCount,
            getPosterUrl(bookNet)
        )
    }

    suspend fun saveBookToFav(book: Book) {
        withContext(Dispatchers.IO) {
            userBooksReference.document(book.id).set(book)
        }
    }

    suspend fun searchBook(query: List<String>, page: Int = 1): ArrayList<Book> {
        val bookList = ArrayList<Book>()

        for (q in query) {
            val bookNet = booksApi.searchCurrentBook(q, BuildConfig.API_KEY)
            val book = Book(
                bookNet.id,
                bookNet.volumeInfo.title,
                bookNet.volumeInfo.subtitle,
                bookNet.volumeInfo.authors,
                bookNet.volumeInfo.publisher,
                bookNet.volumeInfo.publishedDate,
                bookNet.volumeInfo.description,
                bookNet.volumeInfo.pageCount,
                getPosterUrl(bookNet)
            )

            bookList.add(book)
        }
        return bookList
    }

    fun isBookFav(book: Book) {
        databaseUserBooksRef.child(book.id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != "null") {
                    book.isLike = true
                    _currentBook.postValue(book)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}