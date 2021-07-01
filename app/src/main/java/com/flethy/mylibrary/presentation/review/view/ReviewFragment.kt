package com.flethy.mylibrary.presentation.review.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flethy.mylibrary.R
import com.flethy.mylibrary.model.review.entities.Review
import com.flethy.mylibrary.presentation.review.viewmodel.ReviewViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class ReviewFragment : Fragment() {

    private var recycler: RecyclerView? = null

    private lateinit var reviewsAdapter: ReviewsAdapter

    private lateinit var viewModel: ReviewViewModel

    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val reviewsReference = firebaseFirestore.collection("all_reviews")

    companion object {
        fun newInstance() = ReviewFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.review_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViews(parent = view)

        viewModel = ReviewViewModel(requireActivity().application)

        reviewsReference.get().addOnSuccessListener { result ->
            val dataList: ArrayList<Review> = arrayListOf()
            for (document in result) {
                dataList.add(document.toObject())
            }
            reviewsAdapter.bindReviews(dataList)
        }

    }

    private fun findViews(parent: View) {
        recycler = parent.findViewById(R.id.rv_reviews_list)
        reviewsAdapter = ReviewsAdapter(clickListener)
        recycler?.adapter = reviewsAdapter
        recycler?.layoutManager = LinearLayoutManager(context)
    }

    private val clickListener = object : OnReviewItemClicked {

        override fun onItemClick(review: Review) {
            val reviewDetails = ReviewDetailsFragment.newInstance()
            val bundle = Bundle()
            bundle.putSerializable(ReviewDetailsFragment.REVIEW_KEY, review)
            reviewDetails.arguments = bundle

            fragmentManager?.let {
                it.beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.container, reviewDetails)
                    .commit()
            }
        }
    }

}