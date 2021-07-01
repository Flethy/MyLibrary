    package com.flethy.mylibrary.presentation.booklist.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.flethy.mylibrary.BookApp
import com.flethy.mylibrary.R
import com.flethy.mylibrary.model.booklist.entities.Book
import com.flethy.mylibrary.presentation.booklist.viewmodel.BooksViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class BooksAdapter(private val clickListener: OnRecyclerItemClicked) : RecyclerView.Adapter<BooksAdapter.BooksViewHolder>() {

    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val booksReference = firebaseFirestore.collection("all_books")

    private var books: List<Book> = emptyList()

    fun bindBooks(booksList: List<Book>) {
        books = booksList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BooksViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        holder.onBind(books[position])
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(books[position])
        }
    }

    override fun getItemCount() = books.size

    inner class BooksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val bookCover: ImageView = itemView.findViewById(R.id.book_cover)
        private val bookTitle: TextView = itemView.findViewById(R.id.book_title)
        private val bookAuthor: TextView = itemView.findViewById(R.id.book_author)
        private val isLike: ImageView = itemView.findViewById(R.id.like)

        fun onBind(book: Book) {

            bookTitle.text = book.title

            if (book.authors?.joinToString(separator = ", ", prefix = "", postfix = "").toString() == "null") {
                bookAuthor.text = ""
            } else {
                bookAuthor.text = book.authors?.joinToString(separator = ", ", prefix = "", postfix = "").toString()
            }

            bookCover.load(book.thumbnail) {
                crossfade(true)
                crossfade(500)
                placeholder(R.drawable.image_not_found)
            }

            booksReference.get().addOnSuccessListener { result ->
                for (document in result) {
                    if (document.id == book.id) {
                        book.review_count = document.data["review_count"] as Long
                        book.rating = document.data["rating"] as Double
                    }
                }
            }

            isLike.setOnClickListener {
                clickListener.onLikeClick(book)
            }
        }

    }

}

interface OnRecyclerItemClicked {
    fun onLikeClick(book: Book)
    fun onItemClick(book: Book)
}