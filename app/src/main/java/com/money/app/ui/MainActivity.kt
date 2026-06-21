package com.money.app.ui

import com.money.app.R
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.money.app.data.AppDatabase
import com.money.app.data.Transaction
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private var viewFinder: PreviewView? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var flashMode = ImageCapture.FLASH_MODE_OFF
    private var currentZoom = 1.0f
    private var currentActiveNavId: Int = R.id.btnNavHome

    private var speechRecognizer: SpeechRecognizer? = null

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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewFinder = findViewById(R.id.viewFinder)

        setupNavigation()
        
        // Initial landing: Wallet (Management)
        currentActiveNavId = R.id.btnNavHome
        updateNavUI(currentActiveNavId)
        
        findViewById<View>(R.id.homeContent)?.visibility = View.GONE
        findViewById<View>(R.id.fragmentContainer)?.visibility = View.VISIBLE
        
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, WalletFragment())
            .commit()

        findViewById<View>(R.id.btnCapture)?.setOnClickListener {
            takePhotoAndRecognizeText()
        }

        findViewById<View>(R.id.btnGallery)?.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        findViewById<View>(R.id.btnFlip)?.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            if (currentActiveNavId == R.id.btnNavWallet) startCamera()
        }

        findViewById<View>(R.id.btnFlash)?.setOnClickListener {
            flashMode = when (flashMode) {
                ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                else -> ImageCapture.FLASH_MODE_OFF
            }
            updateFlashIcon()
            imageCapture?.flashMode = flashMode
        }

        findViewById<View>(R.id.btnRecord)?.setOnClickListener {
            startVoiceRecognition()
        }

        setupZoomControls()

        cameraExecutor = Executors.newSingleThreadExecutor()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
    }

    private fun setupZoomControls() {
        val z1 = findViewById<TextView>(R.id.tvZoom1)
        val z2 = findViewById<TextView>(R.id.tvZoom2)
        val z5 = findViewById<TextView>(R.id.tvZoom5)

        val updateZoomUI = { zoom: Float ->
            currentZoom = zoom
            camera?.cameraControl?.setZoomRatio(currentZoom)
            z1?.setTextColor(if (zoom == 1.0f) Color.WHITE else ContextCompat.getColor(this, R.color.text_hint))
            z2?.setTextColor(if (zoom == 2.0f) Color.WHITE else ContextCompat.getColor(this, R.color.text_hint))
            z5?.setTextColor(if (zoom == 5.0f) Color.WHITE else ContextCompat.getColor(this, R.color.text_hint))
            
            z1?.paint?.isFakeBoldText = (zoom == 1.0f)
            z2?.paint?.isFakeBoldText = (zoom == 2.0f)
            z5?.paint?.isFakeBoldText = (zoom == 5.0f)
        }

        z1?.setOnClickListener { updateZoomUI(1.0f) }
        z2?.setOnClickListener { updateZoomUI(2.0f) }
        z5?.setOnClickListener { updateZoomUI(5.0f) }
    }

    private fun startVoiceRecognition() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 20)
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Đang lắng nghe... / Listening...")

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                Toast.makeText(this@MainActivity, "Lỗi ghi âm", Toast.LENGTH_SHORT).show()
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    processVoiceText(text)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
        Toast.makeText(this, "Hãy nói gì đó...", Toast.LENGTH_SHORT).show()
    }

    private fun processVoiceText(text: String) {
        val intent = Intent(this, AddTransactionActivity::class.java)
        intent.putExtra("EXTRA_VOICE_TEXT", text)
        startActivity(intent)
    }

    private fun updateFlashIcon() {
        val btnFlash = findViewById<ImageButton>(R.id.btnFlash) ?: return
        when (flashMode) {
            ImageCapture.FLASH_MODE_OFF -> btnFlash.setImageResource(R.drawable.ic_flash_off)
            ImageCapture.FLASH_MODE_ON -> btnFlash.setImageResource(R.drawable.ic_flash_on)
            ImageCapture.FLASH_MODE_AUTO -> btnFlash.setImageResource(R.drawable.ic_flash_on)
        }
    }

    private fun setupNavigation() {
        val btnNavCalendar: View = findViewById(R.id.btnNavCalendar)
        val btnNavHome: View = findViewById(R.id.btnNavHome)
        val btnNavWallet: View = findViewById(R.id.btnNavWallet)
        
        btnNavCalendar.setOnClickListener {
            if (currentActiveNavId != R.id.btnNavCalendar) {
                stopCamera()
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.fragmentContainer, CalendarFragment())
                    .commit()
                currentActiveNavId = R.id.btnNavCalendar
                updateNavUI(currentActiveNavId)
                findViewById<View>(R.id.homeContent)?.visibility = View.GONE
                findViewById<View>(R.id.fragmentContainer)?.visibility = View.VISIBLE
            }
        }

        btnNavHome.setOnClickListener {
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
                findViewById<View>(R.id.homeContent)?.visibility = View.GONE
                findViewById<View>(R.id.fragmentContainer)?.visibility = View.VISIBLE
            }
        }

        btnNavWallet.setOnClickListener {
            if (currentActiveNavId != R.id.btnNavWallet) {
                val homeContent = findViewById<View>(R.id.homeContent)
                val fragContainer = findViewById<View>(R.id.fragmentContainer)
                
                homeContent?.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_in_right))
                fragContainer?.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_out_left))
                
                homeContent?.visibility = View.VISIBLE
                fragContainer?.visibility = View.GONE
                
                currentActiveNavId = R.id.btnNavWallet
                updateNavUI(currentActiveNavId)
                
                if (allPermissionsGranted()) {
                    startCamera()
                } else {
                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
                }
            }
        }
    }

    private fun updateNavUI(activeId: Int) {
        val ivNavCalendar: ImageView = findViewById(R.id.ivNavCalendar)
        val ivNavHome: ImageView = findViewById(R.id.ivNavHome)
        val ivNavWallet: ImageView = findViewById(R.id.ivNavWallet)
        val btnNavCalendar: View = findViewById(R.id.btnNavCalendar)
        val btnNavHome: View = findViewById(R.id.btnNavHome)
        val btnNavWallet: View = findViewById(R.id.btnNavWallet)

        ivNavCalendar.setColorFilter(if (activeId == R.id.btnNavCalendar) Color.WHITE else ContextCompat.getColor(this, R.color.text_hint))
        ivNavHome.setColorFilter(if (activeId == R.id.btnNavHome) Color.WHITE else ContextCompat.getColor(this, R.color.text_hint))
        ivNavWallet.setColorFilter(if (activeId == R.id.btnNavWallet) Color.WHITE else ContextCompat.getColor(this, R.color.text_hint))
        
        btnNavCalendar.background = if (activeId == R.id.btnNavCalendar) ContextCompat.getDrawable(this, R.drawable.bg_nav_active) else null
        btnNavHome.background = if (activeId == R.id.btnNavHome) ContextCompat.getDrawable(this, R.drawable.bg_nav_active) else null
        btnNavWallet.background = if (activeId == R.id.btnNavWallet) ContextCompat.getDrawable(this, R.drawable.bg_nav_active) else null
    }

    private fun startCamera() {
        val viewFinder = viewFinder ?: return
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(viewFinder.surfaceProvider) }
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
                Toast.makeText(this, "Không thể nhận diện văn bản", Toast.LENGTH_SHORT).show()
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

    override fun onResume() {
        super.onResume()
        updateNavUI(currentActiveNavId)
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}