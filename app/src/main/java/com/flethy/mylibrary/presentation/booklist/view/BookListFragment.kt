package com.flethy.mylibrary.presentation.booklist.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flethy.mylibrary.BookApp
import com.flethy.mylibrary.R
import com.flethy.mylibrary.model.booklist.entities.Book
import com.flethy.mylibrary.presentation.booklist.view.BookDetailsFragment.Companion.BOOK_KEY
import com.flethy.mylibrary.presentation.booklist.viewmodel.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class BookListFragment : Fragment() {

    private var btnClose: ImageView? = null
    private var defaultImage: ImageView? = null
    private var defaultText: TextView? = null
    private var etSearchBook: EditText? = null
    private var recycler: RecyclerView? = null
    private var favouriteRecycler: RecyclerView? = null

    private lateinit var booksAdapter: BooksAdapter
    private lateinit var favouriteBooksAdapter: FavouriteBooksAdapter

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val userBooksReference = firebaseFirestore.collection("user_data").document(firebaseAuth.currentUser!!.uid).collection("books")

    private lateinit var viewModel: BooksViewModel

    companion object {
        fun newInstance() = BookListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.booklist_fragment, container, false)

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViews(parent = view)
        setListeners()

        viewModel = (requireActivity().application as BookApp).myComponent.getBooksViewModel(this)

        if (savedInstanceState == null) {
            lifecycleScope.launch {
                viewModel.queryChannel.send("")
            }
        }

        etSearchBook?.doAfterTextChanged {
            lifecycleScope.launch {
                viewModel.queryChannel.send(it.toString())
            }
        }

        userBooksReference.get().addOnSuccessListener { result ->
            val dataList: ArrayList<Book> = arrayListOf()
            for (document in result) {
                dataList.add(document.toObject<Book>())
            }
            favouriteBooksAdapter.bindBooks(dataList)
        }

        viewModel.searchResult.observe(viewLifecycleOwner, { handleBooksList(it) })
    }

    private fun setListeners() {
        btnClose?.setOnClickListener {
            etSearchBook?.setText("")
        }
    }

    override fun onDestroy() {
        deleteViews()
        super.onDestroy()
    }

    private fun handleBooksList(it: BooksResult) {
        when (it) {
            is ValidResult -> {
                recycler?.isVisible = true
                booksAdapter.bindBooks(it.result)
            }
            is ErrorResult -> {
                recycler?.isVisible = false
                Log.e(BookListFragment::class.java.name, "Something went wrong.", it.e)
            }
            is EmptyResult -> {
                recycler?.isVisible = false
            }
            is EmptyQuery -> {
                recycler?.isVisible = false
            }
        }
    }

    private fun findViews(parent: View) {
        etSearchBook = parent.findViewById(R.id.et_search_book)

        btnClose = parent.findViewById(R.id.btn_close)

        defaultImage = parent.findViewById(R.id.default_image)
        defaultText = parent.findViewById(R.id.default_text)

        favouriteRecycler = parent.findViewById(R.id.rv_my_books_list)
        favouriteBooksAdapter = FavouriteBooksAdapter(clickListener)
        favouriteRecycler?.adapter = favouriteBooksAdapter
        favouriteRecycler?.layoutManager = LinearLayoutManager(context)

        recycler = parent.findViewById(R.id.rv_books_list)
        booksAdapter = BooksAdapter(clickListener)
        recycler?.adapter = booksAdapter
        recycler?.layoutManager = LinearLayoutManager(context)
    }

    private fun deleteViews() {
        defaultImage = null
        defaultText = null
        etSearchBook = null
        recycler = null
        favouriteRecycler = null
    }

    private val clickListener = object : OnRecyclerItemClicked {
        override fun onLikeClick(book: Book) {
                recycler?.let { rv ->
                    Snackbar.make(
                        rv,
                        "added to favourite",
                        Snackbar.LENGTH_SHORT)
                        .show()
                }
                viewModel.saveBookToFav(book)
                favouriteBooksAdapter.bindBooks(viewModel.getFavouriteBooks())
        }

        override fun onItemClick(book: Book) {

            val bookDetails = BookDetailsFragment.newInstance()
            val bundle = Bundle()
            bundle.putSerializable(BOOK_KEY, book)
            bookDetails?.arguments = bundle

            fragmentManager?.let {
                it.beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.container, bookDetails)
                    .commit()
            }
        }
    }

}