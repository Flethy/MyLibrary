package com.flethy.mylibrary.presentation.user.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.flethy.mylibrary.R
import com.flethy.mylibrary.model.booklist.entities.Book
import com.flethy.mylibrary.presentation.booklist.view.BookDetailsFragment
import com.flethy.mylibrary.presentation.booklist.view.OnRecyclerItemClicked
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage

class UserFragment: Fragment() {

    private var backButton: ImageView? = null
    private var accountImage: ImageView? = null

    private var recycler: RecyclerView? = null
    private var userBooksAdapter: UserBooksAdapter? = null

    private var userId: String? = null

    companion object {
        fun newInstance() = UserFragment()
        const val USER_KEY = "user"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_fragment, container, false)

        findViews(view)
        setListeners()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_KEY)
        }
        if (userId == null) {
            Snackbar.make(view, "Loading error", Snackbar.LENGTH_SHORT).show()
        } else {
            insertUserData(view)
        }
    }

    private fun insertUserData(view: View) {
        val firebaseFirestore = FirebaseFirestore.getInstance()

        val usersReference = firebaseFirestore.collection("users")
        usersReference.get().addOnSuccessListener { result ->
            for (document in result) {
                if (document.id == userId) {
                    view.findViewById<TextView>(R.id.account_name).text = document.data["full_name"].toString()
                    view.findViewById<TextView>(R.id.user_title).text = document.data["full_name"].toString()
                    break
                }
            }
        }
        val storageReference = FirebaseStorage.getInstance().reference
        val profileRef = storageReference.child("profileImages").child("${userId}.jpg")
        profileRef.downloadUrl.addOnSuccessListener { uri: Uri ->
            accountImage?.load(uri)
        }

        val booksReference = firebaseFirestore.collection("user_data").document(userId.toString()).collection("books")
        booksReference.get().addOnSuccessListener { result ->
            var booksList: ArrayList<Book> = arrayListOf()

            for (document in result) {
                    booksList.add(document.toObject())
            }

            userBooksAdapter?.bindBooks(booksList)
        }

    }

    private fun findViews(view: View?) {
        backButton = view?.findViewById(R.id.back)
        accountImage = view?.findViewById(R.id.account_image)

        recycler = view?.findViewById(R.id.user_books_rv)
        userBooksAdapter = UserBooksAdapter(clickListener)
        recycler?.adapter = userBooksAdapter
        recycler?.layoutManager = GridLayoutManager(context, 3)
    }

    private val clickListener = object : OnRecyclerItemClicked {
        override fun onLikeClick(book: Book) {

        }

        override fun onItemClick(book: Book) {

            val bookDetails = BookDetailsFragment.newInstance()
            val bundle = Bundle()
            bundle.putSerializable(BookDetailsFragment.BOOK_KEY, book)
            bookDetails?.arguments = bundle

            fragmentManager?.let {
                it.beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.container, bookDetails)
                    .commit()
            }
        }
    }

    private fun setListeners() {
        backButton?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

}