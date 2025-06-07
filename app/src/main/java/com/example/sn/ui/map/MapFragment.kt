package com.example.sn.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.sn.R

class MapFragment : Fragment() {

    private lateinit var webView: WebView
    private lateinit var placeSpinner: Spinner

    // URLs for different locations
    private val mapUrls = mapOf(
        "Coimbatore" to "https://app.mappedin.com/map/66db474b3c92f0000b6e59e9?floor=m_e05677566f04df5b",
        "Virudhunagar" to "https://app.mappedin.com/map/67bb66c6416eb8000b2d9b70",
        "erode" to "https://app.mappedin.com/map/67c099b995b3ca000b00823e",
        "tirupur" to "https://app.mappedin.com/map/67c140ba711c92000bcb8bf6",
        "chidambaram" to "https://app.mappedin.com/map/67c0b63fafa669000b087ece"
    )

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // Initialize Spinner and WebView
        placeSpinner = view.findViewById(R.id.placeSpinner)
        webView = view.findViewById(R.id.webView)

        // Set up Spinner (Dropdown Menu)
        val places = listOf("Select a Place", "Coimbatore", "Virudhunagar","erode","tirupur","chidambaram")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, places)
        placeSpinner.adapter = adapter

        // Handle selection from dropdown
        placeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPlace = places[position]
                if (mapUrls.containsKey(selectedPlace)) {
                    loadMappedinMap(mapUrls[selectedPlace]!!)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return view
    }

    // Function to load the selected Mappedin map
    private fun loadMappedinMap(url: String) {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE

        webView.webViewClient = WebViewClient()
        webView.loadUrl(url)
    }
}
