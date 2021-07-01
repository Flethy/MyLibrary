package com.flethy.mylibrary.presentation.timeline.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flethy.mylibrary.R
import com.flethy.mylibrary.model.booklist.entities.Book
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlin.math.roundToInt


class TimelineFragment : Fragment() {

    private var recycler: RecyclerView? = null
    private var timelineBooksAdapter: TimelineBooksAdapter? = null

    companion object {
        fun newInstance() = TimelineFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.timeline_fragment, container, false)

        findViews(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        insertUserData(view)
    }

    private fun insertUserData(view: View) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val booksReference = firebaseFirestore.collection("user_data").document(userId.toString()).collection("books")
        booksReference.get().addOnSuccessListener { result ->
            var booksList: ArrayList<Book> = arrayListOf()

            for (document in result) {
                booksList.add(document.toObject())
            }

            timelineBooksAdapter?.bindBooks(booksList)
        }



        val graph = view.findViewById<BarChart>(R.id.graph)
        val barEntry: ArrayList<BarEntry> = arrayListOf()
        barEntry.add(BarEntry(1f, 3f))
        barEntry.add(BarEntry(2f, 6f))
        barEntry.add(BarEntry(3f, 2f))
        barEntry.add(BarEntry(4f, 1f))
        barEntry.add(BarEntry(5f, 2f))

        val labels = ArrayList<String>()
        labels.add("Февраль")
        labels.add("Март")
        labels.add("Апрель")
        labels.add("Май")
        labels.add("Июнь")

        val barDataSet = BarDataSet(barEntry, "Количество прочитанных книг")
        barDataSet.color = R.color.east_bay_80
        barDataSet.valueTextColor = R.color.transparent
        barDataSet.valueTextSize = 14f
        val barData = BarData(barDataSet)

        graph.axisRight.setDrawAxisLine(false)
        graph.axisLeft.setDrawAxisLine(false)
        graph.xAxis.setDrawAxisLine(false)
        graph.axisRight.setDrawGridLines(false)
        graph.axisLeft.setDrawGridLines(false)
        graph.xAxis.setDrawGridLines(false)

        graph.axisLeft.isEnabled = false
        graph.axisRight.isEnabled = false

        graph.setFitBars(true)
        graph.data = barData
        graph.description.text = ""

        val xAxis: XAxis = graph.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.granularity = 1f

//        xAxis.valueFormatter = LabelFormatter(labels);

        val vf: ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return labels[value.toInt()-1]
            }
        }

        graph.xAxis.valueFormatter = vf
        graph.animateY(1500)

    }


    private fun findViews(view: View?) {
        recycler = view?.findViewById(R.id.my_books_rv)
        timelineBooksAdapter = TimelineBooksAdapter(clickListener)
        recycler?.adapter = timelineBooksAdapter
        recycler?.layoutManager = GridLayoutManager(context, 3)
    }

    private val clickListener = object : OnBookItemClicked {
        override fun onItemClick(book: Book) {

            val timelineDetails = TimelineDetailsFragment.newInstance()
            val bundle = Bundle()
            bundle.putSerializable(TimelineDetailsFragment.BOOK_KEY, book)
            timelineDetails?.arguments = bundle

            fragmentManager?.let {
                it.beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.container, timelineDetails)
                    .commit()
            }
        }
    }
}

//class LabelFormatter(private val mLabels: ArrayList<String>) :
//    ValueFormatter() {
//    override fun getFormattedValue(value: Float, axis: AxisBase): String {
//        return mLabels[value.toInt()]
//    }
//}