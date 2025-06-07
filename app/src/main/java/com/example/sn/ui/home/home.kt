package com.example.sn.ui.home



import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.*

import com.example.sn.databinding.FragmentScannerBinding

class ScannerFragment : Fragment() {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 101)
        } else {
            startScanner() // Start scanner if permission is already granted
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScanner()
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startScanner() {
        val scannerView: CodeScannerView = binding.scannerView
        codeScanner = CodeScanner(requireContext(), scannerView)

        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback { result ->
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), "Scanned: ${result.text}", Toast.LENGTH_SHORT).show()
            }
        }

        codeScanner.errorCallback = ErrorCallback { error ->
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), "Camera error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        scannerView.post {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner.startPreview()
        } else {
            startScanner()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::codeScanner.isInitialized) {
            codeScanner.stopPreview() // Pause instead of releasing resources
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources() // Release only on destroy
        }
        _binding = null
    }
}
