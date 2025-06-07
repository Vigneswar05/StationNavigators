package com.example.sn.ui.schedule

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.sn.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class ScheduleFragment : Fragment() {


    private lateinit var tableLayoutSchedule: TableLayout
    private lateinit var spinnerScheduleLocations: Spinner
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        tableLayoutSchedule = view.findViewById(R.id.tableLayoutSchedule)
        spinnerScheduleLocations = view.findViewById(R.id.spinnerScheduleLocations)

        // List of locations
        val locations = listOf("Coimbatore", "Virudhunagar")

        // Set up Spinner (Dropdown)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerScheduleLocations.adapter = adapter

        // Fetch data when a location is selected
        spinnerScheduleLocations.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLocation = locations[position].lowercase() // Convert to lowercase for Firestore
                fetchScheduleData(selectedLocation)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return view
    }

    private fun fetchScheduleData(location: String) {
        tableLayoutSchedule.removeAllViews()

        try {
            db.collection("Schedule").document(location)
                .collection("details")
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(requireContext(), "No schedule data found for $location", Toast.LENGTH_SHORT).show()
                        println("Firestore: No data found in details collection for $location")
                        return@addOnSuccessListener
                    }

                    // Debugging Log: Check if documents exist
                    println("Firestore: Successfully retrieved ${documents.size()} train schedules for $location")

                    // Add Table Header
                    val headerRow = TableRow(requireContext())
                    headerRow.addView(createTextView("Train No."))
                    headerRow.addView(createTextView("Departure"))
                    headerRow.addView(createTextView("Platform"))
                    tableLayoutSchedule.addView(headerRow)
                    tableLayoutSchedule.addView(createSeparator())

                    // Add Train Schedule Rows
                    for (document in documents) {
                        val number = document.getString("number") ?: "N/A"

                        // Convert Firestore Timestamp to formatted String
                        val departureTimestamp = document.getTimestamp("departure")
                        val departure: String = if (departureTimestamp != null) {
                            formatTimestamp(departureTimestamp)
                        } else {
                            "N/A"
                        }

                        val platform = document.getString("platform") ?: "N/A"

                        // Debugging Log: Print each train's data
                        println("Firestore: Train - Number: $number, Departure: $departure, Platform: $platform")

                        val dataRow = TableRow(requireContext())
                        dataRow.addView(createTextView(number))
                        dataRow.addView(createTextView(departure))
                        dataRow.addView(createTextView(platform))

                        tableLayoutSchedule.addView(dataRow)
                        tableLayoutSchedule.addView(createSeparator())
                    }
                }
                .addOnFailureListener { exception ->
                    println("Firestore Error: ${exception.message}")
                    Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            println("Unexpected Error: ${e.message}")
            Toast.makeText(requireContext(), "Unexpected Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun createTextView(text: String): TextView {
        val textView = TextView(requireContext())
        textView.text = text
        textView.setPadding(16, 16, 16, 16)
        textView.setTypeface(null, android.graphics.Typeface.BOLD)
        textView.textSize = 16f
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        return textView
    }

    private fun createSeparator(): View {
        val separator = View(requireContext())
        separator.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2)
        separator.setBackgroundColor(Color.GRAY)
        return separator
    }
    private fun formatTimestamp(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()) // 12-hour format with AM/PM
        return sdf.format(timestamp.toDate()) // Convert Timestamp to Date, then format it
    }

}
