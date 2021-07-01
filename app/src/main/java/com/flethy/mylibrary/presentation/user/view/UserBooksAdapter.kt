package com.flethy.mylibrary.presentation.user.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.flethy.mylibrary.R
import com.flethy.mylibrary.model.booklist.entities.Book
import com.flethy.mylibrary.presentation.booklist.view.BooksAdapter
import com.flethy.mylibrary.presentation.booklist.view.OnRecyclerItemClicked

class UserBooksAdapter(private val clickListener: OnRecyclerItemClicked) : RecyclerView.Adapter<UserBooksAdapter.UserBooksViewHolder>() {

    private var books: List<Book> = emptyList()

    fun bindBooks(booksList: List<Book>) {
        books = booksList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserBooksAdapter.UserBooksViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.item_user_book, parent, false)
        return UserBooksViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: UserBooksAdapter.UserBooksViewHolder, position: Int) {
        holder.onBind(books[position])
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(books[position])
        }
    }

    override fun getItemCount() = books.size

    inner class UserBooksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(book: Book) {
            itemView.findViewById<ImageView>(R.id.book_cover).load(book.thumbnail)
        }
    }

}