package com.example.sn.ui.voicerecognition

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sn.R
import com.example.sn.databinding.FragmentVoiceRecognitionBinding

class VoiceRecognitionFragment : Fragment() {

    private var _binding: FragmentVoiceRecognitionBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceRecognitionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // When button is clicked, start voice recognition
        binding.btnStartVoice.setOnClickListener {
            checkPermissions()
        }

        return root
    }

    /**
     * Check microphone permissions before starting voice recognition.
     */
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        } else {
            startVoiceRecognition()
        }
    }

    /**
     * Handle permissions result.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecognition()
            } else {
                Toast.makeText(requireContext(), "Microphone permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Start Google Speech-to-Text
     */
    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")

        try {
            startActivityForResult(intent, REQUEST_RECORD_AUDIO_PERMISSION)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Voice recognition not supported on this device", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Handle voice recognition results.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!result.isNullOrEmpty()) {
                val spokenText = result[0].lowercase()
                Toast.makeText(requireContext(), "Recognized: $spokenText", Toast.LENGTH_SHORT).show()
                navigateToFragment(spokenText)
            }
        }
    }

    /**
     * Navigate to the respective fragment based on the voice command.
     */
    private fun navigateToFragment(command: String) {
        val navController = findNavController()

        when {
            command.contains("home", ignoreCase = true) -> navController.navigate(R.id.nav_home)
            command.contains("porters", ignoreCase = true) -> navController.navigate(R.id.nav_porters)
            command.contains("schedule", ignoreCase = true) -> navController.navigate(R.id.nav_schedule)
            command.contains("scanner", ignoreCase = true) -> navController.navigate(R.id.nav_scanner)
            command.contains("map", ignoreCase = true) -> navController.navigate(R.id.nav_map)
            else -> Toast.makeText(requireContext(), "Command not recognized", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
