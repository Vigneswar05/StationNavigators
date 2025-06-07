package com.example.sn.ui.scanner
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.example.sn.R
class GalleryFragment : Fragment() {
    private lateinit var textViewPorters: TextView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)
        textViewPorters = view.findViewById(R.id.textViewPorters)
        fetchData()
        return view
    }

    private fun fetchData() {
        db.collection("porters")
            .get()
            .addOnSuccessListener { documents ->
                val stringBuilder = StringBuilder()
                for (document in documents) {
                    val name = document.getString("name") ?: "Unknown"
                    val age = document.getLong("age") ?: 0
                    stringBuilder.append("Name: $name, Age: $age\n")
                }
                textViewPorters.text = stringBuilder.toString()
            }
            .addOnFailureListener { e ->
                textViewPorters.text = "Error loading data: ${e.message}"
            }
    }

}
