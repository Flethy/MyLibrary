package com.flethy.mylibrary.presentation.timeline.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.flethy.mylibrary.R
import com.flethy.mylibrary.model.booklist.entities.Book


class TimelineBooksAdapter(private val clickListener: OnBookItemClicked) : RecyclerView.Adapter<TimelineBooksAdapter.TimelineBooksViewHolder>() {

    private var books: List<Book> = emptyList()

    fun bindBooks(booksList: List<Book>) {
        books = booksList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineBooksAdapter.TimelineBooksViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.item_user_book, parent, false)
        return TimelineBooksViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: TimelineBooksAdapter.TimelineBooksViewHolder, position: Int) {
        holder.onBind(books[position])
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(books[position])
        }
    }

    override fun getItemCount() = books.size

    inner class TimelineBooksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(book: Book) {
            itemView.findViewById<ImageView>(R.id.book_cover).load(book.thumbnail)
        }
    }

}

interface OnBookItemClicked {
    fun onItemClick(book: Book)
}