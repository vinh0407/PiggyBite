package com.money.app.ui

import com.money.app.R
import com.money.app.databinding.ActivityMainBinding
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.money.app.util.FirebaseSyncManager
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Main Entry activity of the application after authentication.
 * Handles primary navigation (Calendar, Home/Wallet, AI Camera) and global sync triggers.
 * 
 * Architecture: AppCompatActivity with ViewBinding
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var flashMode = ImageCapture.FLASH_MODE_OFF
    private var currentZoom = 1.0f
    private var currentActiveNavId: Int = R.id.btnNavHome
    private var speechRecognizer: SpeechRecognizer? = null
    private val auth = FirebaseAuth.getInstance()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                val photoFile = java.io.File(externalCacheDir, "gallery_image_${System.currentTimeMillis()}.jpg")
                contentResolver.openInputStream(uri)?.use { input ->
                    photoFile.outputStream().use { output -> input.copyTo(output) }
                }
                recognizeText(photoFile)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        com.money.app.util.ThemeHelper.applyTheme(this)
        super.onCreate(savedInstanceState)

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        currentActiveNavId = prefs.getInt("last_nav_id", R.id.btnNavHome)

        lifecycleScope.launch {
            try {
                val syncManager = FirebaseSyncManager(this@MainActivity)
                
                // Check for new invitations for current email
                val userEmail = auth.currentUser?.email
                val userId = auth.currentUser?.uid
                if (!userEmail.isNullOrEmpty() && userId != null) {
                    syncManager.checkPendingInvitations(userEmail, userId)
                }

                syncManager.syncTransactions()
                syncManager.syncFunds()
            } catch (e: Exception) {
                Log.e("Sync", "Initial sync failed: ${e.message}")
                Toast.makeText(this@MainActivity, "Đồng bộ dữ liệu không thành công. Vui lòng kiểm tra mạng.", Toast.LENGTH_SHORT).show()
            }
        }

        setupNavigation()
        
        updateNavUI(currentActiveNavId)
        
        // Restore last fragment
        when (currentActiveNavId) {
            R.id.btnNavCalendar -> {
                binding.homeContent.visibility = View.GONE
                binding.fragmentContainer.visibility = View.VISIBLE
                supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, CalendarFragment()).commit()
            }
            R.id.btnNavWallet -> {
                binding.homeContent.visibility = View.VISIBLE
                binding.fragmentContainer.visibility = View.GONE
                if (allPermissionsGranted()) startCamera()
            }
            else -> {
                binding.homeContent.visibility = View.GONE
                binding.fragmentContainer.visibility = View.VISIBLE
                supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, WalletFragment()).commit()
            }
        }

        binding.btnCapture.setOnClickListener {
            takePhotoAndRecognizeText()
        }

        binding.btnGallery.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnFlip.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            if (currentActiveNavId == R.id.btnNavWallet) startCamera()
        }

        binding.btnFlash.setOnClickListener {
            flashMode = when (flashMode) {
                ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                else -> ImageCapture.FLASH_MODE_OFF
            }
            updateFlashIcon()
            imageCapture?.flashMode = flashMode
        }

        binding.btnVoice.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 20)
            } else {
                showVoiceInputDialog()
            }
        }

        setupZoomControls()
        cameraExecutor = Executors.newSingleThreadExecutor()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
    }

    private fun showVoiceInputDialog() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Thiết bị không hỗ trợ nhận diện giọng nói. Vui lòng cài đặt ứng dụng Google.", Toast.LENGTH_LONG).show()
            return
        }

        val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_voice_input, null)
        dialog.setContentView(view)

        val tvStatus = view.findViewById<TextView>(R.id.tvVoiceStatus)
        val tvLiveText = view.findViewById<TextView>(R.id.tvLiveText)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        }

        // Explicitly try to use Google recognition service for best stability
        speechRecognizer?.destroy()
        speechRecognizer = try {
            SpeechRecognizer.createSpeechRecognizer(this, android.content.ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/com.google.android.voicesearch.service.SpeechRecognitionService"))
        } catch (e: Exception) {
            SpeechRecognizer.createSpeechRecognizer(this)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                tvStatus.text = "PiggyBite đang nghe..."
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                tvStatus.text = "Đang xử lý dữ liệu..."
            }
            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Lỗi âm thanh. Hãy thử lại."
                    SpeechRecognizer.ERROR_CLIENT -> "Lỗi kết nối. Hãy kiểm tra mạng."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Cần quyền Micro trong cài đặt."
                    SpeechRecognizer.ERROR_NETWORK -> "Lỗi mạng Internet."
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Kết nối mạng quá chậm."
                    SpeechRecognizer.ERROR_NO_MATCH -> "Không nghe rõ, hãy thử lại."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Hệ thống đang bận."
                    SpeechRecognizer.ERROR_SERVER -> "Lỗi máy chủ nhận diện."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Bạn chưa nói gì cả."
                    else -> "Lỗi hệ thống ($error)"
                }
                tvStatus.text = message
                Log.e("Voice", "Error: $error - $message")
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    tvLiveText.text = text
                    view.postDelayed({
                        if (dialog.isShowing) {
                            dialog.dismiss()
                            val addIntent = Intent(this@MainActivity, AddTransactionActivity::class.java)
                            addIntent.putExtra("EXTRA_VOICE_TEXT", text)
                            startActivity(addIntent)
                        }
                    }, 800)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    tvLiveText.text = matches[0]
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
        dialog.show()

        dialog.setOnDismissListener {
            speechRecognizer?.stopListening()
        }
    }

    private fun setupZoomControls() {
        val updateZoomUI = { zoom: Float ->
            currentZoom = zoom
            camera?.cameraControl?.setZoomRatio(currentZoom)
            binding.tvZoom1.setTextColor(if (zoom == 1.0f) Color.WHITE else ContextCompat.getColor(this, R.color.text_hint))
            binding.tvZoom2.setTextColor(if (zoom == 2.0f) Color.WHITE else ContextCompat.getColor(this, R.color.text_hint))
            binding.tvZoom5.setTextColor(if (zoom == 5.0f) Color.WHITE else ContextCompat.getColor(this, R.color.text_hint))
        }

        binding.tvZoom1.setOnClickListener { updateZoomUI(1.0f) }
        binding.tvZoom2.setOnClickListener { updateZoomUI(2.0f) }
        binding.tvZoom5.setOnClickListener { updateZoomUI(5.0f) }
    }

    private fun updateFlashIcon() {
        when (flashMode) {
            ImageCapture.FLASH_MODE_OFF -> binding.btnFlash.setImageResource(R.drawable.ic_flash_off)
            ImageCapture.FLASH_MODE_ON -> binding.btnFlash.setImageResource(R.drawable.ic_flash_on)
            else -> binding.btnFlash.setImageResource(R.drawable.ic_flash_on)
        }
    }

    private fun setupNavigation() {
        binding.btnNavCalendar.setOnClickListener {
            if (currentActiveNavId != R.id.btnNavCalendar) {
                stopCamera()
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.fragmentContainer, CalendarFragment())
                    .commit()
                currentActiveNavId = R.id.btnNavCalendar
                updateNavUI(currentActiveNavId)
                binding.homeContent.visibility = View.GONE
                binding.fragmentContainer.visibility = View.VISIBLE
            }
        }

        binding.btnNavHome.setOnClickListener {
            if (currentActiveNavId != R.id.btnNavHome) {
                stopCamera()
                val animIn = if (currentActiveNavId == R.id.btnNavCalendar) R.anim.slide_in_right else R.anim.slide_in_left
                val animOut = if (currentActiveNavId == R.id.btnNavCalendar) R.anim.slide_out_left else R.anim.slide_out_right
                
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(animIn, animOut)
                    .replace(R.id.fragmentContainer, WalletFragment())
                    .commit()
                
                currentActiveNavId = R.id.btnNavHome
                updateNavUI(currentActiveNavId)
                binding.homeContent.visibility = View.GONE
                binding.fragmentContainer.visibility = View.VISIBLE
            }
        }

        binding.btnNavWallet.setOnClickListener {
            if (currentActiveNavId != R.id.btnNavWallet) {
                binding.homeContent.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_in_right))
                binding.fragmentContainer.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_out_left))
                
                binding.homeContent.visibility = View.VISIBLE
                binding.fragmentContainer.visibility = View.GONE
                
                currentActiveNavId = R.id.btnNavWallet
                updateNavUI(currentActiveNavId)
                
                if (allPermissionsGranted()) startCamera()
                else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)
            }
        }
    }

    private fun updateNavUI(activeId: Int) {
        binding.ivNavCalendar.setColorFilter(if (activeId == R.id.btnNavCalendar) Color.WHITE else ContextCompat.getColor(this, R.color.text_hint))
        binding.ivNavHome.setColorFilter(if (activeId == R.id.btnNavHome) Color.WHITE else ContextCompat.getColor(this, R.color.text_hint))
        binding.ivNavWallet.setColorFilter(if (activeId == R.id.btnNavWallet) Color.WHITE else ContextCompat.getColor(this, R.color.text_hint))
        
        binding.btnNavCalendar.background = if (activeId == R.id.btnNavCalendar) ContextCompat.getDrawable(this, R.drawable.bg_nav_active) else null
        binding.btnNavHome.background = if (activeId == R.id.btnNavHome) ContextCompat.getDrawable(this, R.drawable.bg_nav_active) else null
        binding.btnNavWallet.background = if (activeId == R.id.btnNavWallet) ContextCompat.getDrawable(this, R.drawable.bg_nav_active) else null
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(binding.viewFinder.surfaceProvider) }
            imageCapture = ImageCapture.Builder().setFlashMode(flashMode).setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                camera?.cameraControl?.setZoomRatio(currentZoom)
            } catch (exc: Exception) {
                Log.e("Camera", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera() {
        ProcessCameraProvider.getInstance(this).get().unbindAll()
        camera = null
    }

    private fun takePhotoAndRecognizeText() {
        val imageCapture = imageCapture ?: return
        val photoFile = java.io.File(externalCacheDir, "ocr_image_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("OCR", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    recognizeText(photoFile)
                }
            }
        )
    }

    private fun recognizeText(photoFile: java.io.File) {
        try {
            val image = InputImage.fromFilePath(this, android.net.Uri.fromFile(photoFile))
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val intent = Intent(this, AddTransactionActivity::class.java)
                    intent.putExtra("EXTRA_FROM_CAPTURE", true)
                    intent.putExtra("EXTRA_IMAGE_PATH", photoFile.absolutePath)
                    intent.putExtra("EXTRA_OCR_TEXT", visionText.text)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.e("OCR", "Text recognition failed", e)
                    Toast.makeText(this, "Không thể nhận diện văn bản: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Log.e("OCR", "Error opening image file", e)
            Toast.makeText(this, "Lỗi khi mở ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        speechRecognizer?.destroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10 && allPermissionsGranted()) startCamera()
        if (requestCode == 20 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) showVoiceInputDialog()
    }

    override fun onPause() {
        super.onPause()
        getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().putInt("last_nav_id", currentActiveNavId).apply()
    }

    override fun onResume() {
        super.onResume()
        updateNavUI(currentActiveNavId)
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}