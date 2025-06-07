package com.example.sn.ui.home
import androidx.navigation.fragment.findNavController
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
import androidx.lifecycle.ViewModelProvider
import com.example.sn.R
import com.example.sn.databinding.FragmentSlideshowBinding
import com.example.sn.ui.map.MapFragment
import com.example.sn.ui.porters.PortersFragment
import com.example.sn.ui.schedule.ScheduleFragment
import com.example.sn.ui.scanner.ScannerFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel = ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set ImageView resource dynamically
        binding.imageView.setImageResource(R.drawable.logo)
        binding.
        textView3.text = "An easy way to navigate through the railway station and check the train schedule and book the porters." +
                "Select the location of yours and view the indoor railway station map and navigate easily." +
                "View the porter details of your respective loaction." +
                "Check the train schelude of which ever place you want."


        // Voice Recognition Button
        binding.btnVoice.setOnClickListener {
            checkPermissions()
        }
        binding.btnScanner.setOnClickListener {
            findNavController().navigate(R.id.nav_scanner)
        }
        binding.btnMap.setOnClickListener {
            findNavController().navigate(R.id.nav_map)
        }

        return root
    }

    /**
     * Check for microphone permissions before starting voice recognition.
     */
    private fun checkPermissions() {
        val permissionStatus = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Microphone permission already granted!", Toast.LENGTH_SHORT).show()
            startVoiceRecognition()
        } else {
            Toast.makeText(requireContext(), "Requesting microphone permission...", Toast.LENGTH_SHORT).show()

            // Check if the user has permanently denied permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(requireContext(), "Please enable microphone permission in settings.", Toast.LENGTH_LONG).show()
            } else {
                // Request permission
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_PERMISSION
                )
            }
        }
    }

    /**
     * Handle permissions result.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Microphone permission granted!", Toast.LENGTH_SHORT).show()
                startVoiceRecognition()
            } else {
                Toast.makeText(requireContext(), "Microphone permission denied. Please enable it in settings.", Toast.LENGTH_LONG).show()
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
            println("Error: ${e.message}")
        }
    }

    /**
     * Handle voice recognition results.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

            if (result.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Could not recognize speech. Try again!", Toast.LENGTH_SHORT).show()
                return
            }

            val spokenText = result[0].lowercase()
            Toast.makeText(requireContext(), "Recognized: $spokenText", Toast.LENGTH_SHORT).show()
            handleVoiceCommand(spokenText)
        }
    }


    /**
     * Process voice command and navigate to respective fragment.
     */
    private fun handleVoiceCommand(command: String) {
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
}
