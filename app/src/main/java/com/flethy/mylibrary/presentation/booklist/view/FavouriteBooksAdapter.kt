package com.flethy.mylibrary.presentation.booklist.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.flethy.mylibrary.R
import com.flethy.mylibrary.model.booklist.entities.Book
import com.google.firebase.firestore.FirebaseFirestore

class FavouriteBooksAdapter(private val clickListener: OnRecyclerItemClicked) : RecyclerView.Adapter<FavouriteBooksViewHolder>() {

    private var books: List<Book> = emptyList()

    fun bindBooks(booksList: List<Book>) {
        books = booksList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteBooksViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.item_favourite_book, parent, false)
        return FavouriteBooksViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: FavouriteBooksViewHolder, position: Int) {
        holder.onBind(books[position])
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(books[position])
        }
    }

    override fun getItemCount() = books.size

}

class FavouriteBooksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val booksReference = firebaseFirestore.collection("all_books")

    private val bookCover: ImageView = itemView.findViewById(R.id.book_cover)
    private val bookTitle: TextView = itemView.findViewById(R.id.book_title)
    private val bookAuthor: TextView = itemView.findViewById(R.id.book_author)

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

    }

}