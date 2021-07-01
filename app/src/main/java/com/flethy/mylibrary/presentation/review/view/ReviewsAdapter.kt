package com.flethy.mylibrary.presentation.review.view

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.flethy.mylibrary.BookApp
import com.flethy.mylibrary.R
import com.flethy.mylibrary.model.booklist.entities.Book
import com.flethy.mylibrary.model.review.entities.Review
import com.flethy.mylibrary.presentation.booklist.view.OnRecyclerItemClicked
import com.flethy.mylibrary.presentation.booklist.viewmodel.BooksViewModel
import com.flethy.mylibrary.presentation.review.viewmodel.ReviewViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class ReviewsAdapter(private val clickListener: OnReviewItemClicked) : RecyclerView.Adapter<ReviewsViewHolder>() {

    private var reviews: List<Review> = emptyList()

    fun bindReviews(reviewsList: ArrayList<Review>) {
        reviews = reviewsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewsViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        holder.onBind(reviews[position])
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(reviews[position])
        }
    }

    override fun getItemCount() = reviews.size

}

class ReviewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val reviewsReference = firebaseFirestore.collection("all_reviews")
    private val booksReference = firebaseFirestore.collection("all_books")

    private val bookCover: ImageView = itemView.findViewById(R.id.book_cover)
    private val bookTitle: TextView = itemView.findViewById(R.id.book_title)
    private val bookAuthor: TextView = itemView.findViewById(R.id.book_author)
    private val accountImage: ImageView = itemView.findViewById(R.id.account_image)
    private val reviewText: TextView = itemView.findViewById(R.id.review_text)
    private val accountName: TextView = itemView.findViewById(R.id.account_name)
    private val ratingBar: RatingBar = itemView.findViewById(R.id.rating_bar)

    fun onBind(review: Review) {

        GlobalScope.launch {

            reviewText.text = review.reviewText
            ratingBar.rating = review.rating.toFloat()

            booksReference.get().addOnSuccessListener { result ->
                for (document in result) {
                    if (document.id == review.bookId) {
                        val book: Book = document.toObject()
                        if (book?.authors?.joinToString(separator = ", ", prefix = "", postfix = "").toString() == "null") {
                            bookAuthor.text = ""
                        } else {
                            bookAuthor.text = book?.authors?.joinToString(separator = ", ", prefix = "", postfix = "").toString()
                        }

                        bookCover.load(book?.thumbnail)
                        bookTitle.text = book?.title
                        break
                    }
                }
            }

            val usersReference = firebaseFirestore.collection("users")
            usersReference.get().addOnSuccessListener { result ->
                for (document in result) {
                    if (document.id == review.userId) {
                        accountName.text = document.data["full_name"].toString()
                        break
                    }
                }
            }

            val storageReference = FirebaseStorage.getInstance().reference
            val profileRef = storageReference.child("profileImages").child("${review.userId}.jpg")
            profileRef.downloadUrl.addOnSuccessListener { uri: Uri ->
                accountImage?.load(uri)
            }
        }
    }

}

interface OnReviewItemClicked {
    fun onItemClick(review: Review)
}