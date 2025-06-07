package com.example.sn.ui.porters

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.sn.R
import com.google.firebase.firestore.FirebaseFirestore

class PortersFragment : Fragment() {

    private lateinit var tableLayoutPorters: TableLayout
    private lateinit var spinnerLocations: Spinner
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_porters, container, false)

        tableLayoutPorters = view.findViewById(R.id.tableLayoutPorters)
        spinnerLocations = view.findViewById(R.id.spinnerLocations)

        // List of locations
        val locations = listOf("Coimbatore", "Virudhunagar")

        // Set up Spinner (Dropdown)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLocations.adapter = adapter

        // Fetch data when a location is selected
        spinnerLocations.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLocation = locations[position].lowercase() // Convert to lowercase for Firestore
                fetchData(selectedLocation)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return view
    }

    private fun fetchData(location: String) {
        tableLayoutPorters.removeAllViews()

        // Fetch porters under the selected location
        db.collection("porters").document(location)
            .collection("details")
            .get()
            .addOnSuccessListener { documents ->
                // Add Table Header
                val headerRow = TableRow(requireContext())
                headerRow.addView(createTextView("Name"))
                headerRow.addView(createTextView("Phone"))
                headerRow.addView(createTextView("Cost"))
                headerRow.addView(createTextView("Platform"))
                tableLayoutPorters.addView(headerRow)
                tableLayoutPorters.addView(createSeparator())

                // Add Porter Data Rows
                for (document in documents) {
                    val name = document.getString("name") ?: "Unknown"
                    val phone = document.getString("phone") ?: "No Phone"
                    val cost = document.getString("cost") ?: "No Cost"
                    val platform = document.getString("Platform") ?: "No Platform"

                    val dataRow = TableRow(requireContext())
                    dataRow.addView(createTextView(name))
                    dataRow.addView(createTextView(phone))
                    dataRow.addView(createTextView("₹$cost"))
                    dataRow.addView(createTextView(platform))

                    tableLayoutPorters.addView(dataRow)
                    tableLayoutPorters.addView(createSeparator())
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
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
}
