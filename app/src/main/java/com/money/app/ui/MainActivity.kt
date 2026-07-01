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
 * Hoạt động chính (Main Activity): Màn hình chính sau khi đăng nhập.
 * Đóng vai trò là trung tâm điều hướng và xử lý các tính năng thông minh:
 * - Điều hướng giữa các Fragment: Lịch (Calendar), Trang chủ (Home), Camera AI.
 * - Tích hợp CameraX để quét hóa đơn và nhận diện văn bản (OCR).
 * - Nhận diện giọng nói để thêm nhanh giao dịch.
 * - Kích hoạt đồng bộ dữ liệu Firebase khi khởi chạy ứng dụng.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var flashMode = ImageCapture.FLASH_MODE_OFF
    private var currentZoom = 1.0f
    private var currentActiveNavId: Int = R.id.btnNavHome // ID của Tab đang hoạt động
    private var speechRecognizer: SpeechRecognizer? = null
    private val auth = FirebaseAuth.getInstance()

    // Bộ chọn ảnh từ thư viện để quét OCR
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                val photoFile = java.io.File(externalCacheDir, "gallery_image_${System.currentTimeMillis()}.jpg")
                contentResolver.openInputStream(uri)?.use { input ->
                    photoFile.outputStream().use { output -> input.copyTo(output) }
                }
                recognizeText(photoFile) // Nhận diện văn bản từ ảnh được chọn
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        com.money.app.util.ThemeHelper.applyTheme(this) // Áp dụng chủ đề người dùng đã chọn
        super.onCreate(savedInstanceState)

        // Kiểm tra đăng nhập
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tải lại Tab cuối cùng mà người dùng đã mở
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        currentActiveNavId = prefs.getInt("last_nav_id", R.id.btnNavHome)

        // Đồng bộ hóa dữ liệu từ Firebase
        lifecycleScope.launch {
            try {
                val syncManager = FirebaseSyncManager(this@MainActivity)
                val userEmail = auth.currentUser?.email
                val userId = auth.currentUser?.uid
                if (!userEmail.isNullOrEmpty() && userId != null) {
                    syncManager.checkPendingInvitations(userEmail, userId)
                }
                syncManager.syncTransactions()
                syncManager.syncFunds()
            } catch (e: Exception) {
                Log.e("Sync", "Initial sync failed: ${e.message}")
            }
        }

        setupNavigation() // Cài đặt sự kiện cho thanh Bottom Navigation
        updateNavUI(currentActiveNavId) // Cập nhật trạng thái hiển thị của nút Nav
        
        // Hiển thị Fragment tương ứng với Tab được chọn
        restoreLastFragment()

        // Các nút điều khiển Camera AI
        binding.btnCapture.setOnClickListener { takePhotoAndRecognizeText() }
        binding.btnGallery.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.btnFlip.setOnClickListener { flipCamera() }
        binding.btnFlash.setOnClickListener { toggleFlash() }

        // Nút thêm giao dịch bằng giọng nói
        binding.btnVoice.setOnClickListener { checkAudioPermissionAndShowVoiceDialog() }

        setupZoomControls() // Cài đặt các mức Zoom (1x, 2x, 5x)
        cameraExecutor = Executors.newSingleThreadExecutor()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
    }

    /**
     * Khôi phục Fragment gần nhất mà người dùng sử dụng
     */
    private fun restoreLastFragment() {
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
    }

    /**
     * Hiển thị BottomSheet ghi âm và xử lý nhận diện giọng nói tiếng Việt
     */
    private fun showVoiceInputDialog() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Thiết bị không hỗ trợ nhận diện giọng nói.", Toast.LENGTH_LONG).show()
            return
        }

        val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_voice_input, null)
        dialog.setContentView(view)

        val tvStatus = view.findViewById<TextView>(R.id.tvVoiceStatus)
        val tvLiveText = view.findViewById<TextView>(R.id.tvLiveText)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN") // Nhận diện tiếng Việt
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { tvStatus.text = "PiggyBite đang nghe..." }
            override fun onEndOfSpeech() { tvStatus.text = "Đang xử lý dữ liệu..." }
            override fun onError(error: Int) { tvStatus.text = "Không nghe rõ, hãy thử lại." }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    tvLiveText.text = text
                    view.postDelayed({
                        if (dialog.isShowing) {
                            dialog.dismiss()
                            // Mở màn hình Thêm giao dịch với văn bản đã nói
                            val addIntent = Intent(this@MainActivity, AddTransactionActivity::class.java)
                            addIntent.putExtra("EXTRA_VOICE_TEXT", text)
                            startActivity(addIntent)
                        }
                    }, 800)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) tvLiveText.text = matches[0]
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
        dialog.show()
        dialog.setOnDismissListener { speechRecognizer?.stopListening() }
    }

    private fun checkAudioPermissionAndShowVoiceDialog() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 20)
        } else {
            showVoiceInputDialog()
        }
    }

    private fun flipCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
        if (currentActiveNavId == R.id.btnNavWallet) startCamera()
    }

    private fun toggleFlash() {
        flashMode = when (flashMode) {
            ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
            else -> ImageCapture.FLASH_MODE_OFF
        }
        updateFlashIcon()
        imageCapture?.flashMode = flashMode
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

    /**
     * Cài đặt logic chuyển đổi giữa các màn hình chính (Navigation)
     */
    private fun setupNavigation() {
        binding.btnNavCalendar.setOnClickListener { switchFragment(R.id.btnNavCalendar, CalendarFragment()) }
        binding.btnNavHome.setOnClickListener { switchFragment(R.id.btnNavHome, WalletFragment()) }
        binding.btnNavWallet.setOnClickListener { 
            if (currentActiveNavId != R.id.btnNavWallet) {
                stopCamera()
                binding.homeContent.visibility = View.VISIBLE
                binding.fragmentContainer.visibility = View.GONE
                currentActiveNavId = R.id.btnNavWallet
                updateNavUI(currentActiveNavId)
                if (allPermissionsGranted()) startCamera()
                else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)
            }
        }
    }

    private fun switchFragment(id: Int, fragment: androidx.fragment.app.Fragment) {
        if (currentActiveNavId != id) {
            stopCamera()
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.fragmentContainer, fragment)
                .commit()
            currentActiveNavId = id
            updateNavUI(currentActiveNavId)
            binding.homeContent.visibility = View.GONE
            binding.fragmentContainer.visibility = View.VISIBLE
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

    /**
     * Khởi tạo luồng Camera bằng CameraX
     */
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
        try {
            ProcessCameraProvider.getInstance(this).get().unbindAll()
        } catch (e: Exception) {}
        camera = null
    }

    /**
     * Chụp ảnh hóa đơn và gửi đi nhận diện văn bản (OCR)
     */
    private fun takePhotoAndRecognizeText() {
        val imageCapture = imageCapture ?: return
        val photoFile = java.io.File(externalCacheDir, "ocr_image_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) { Log.e("OCR", "Capture failed: ${exc.message}") }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) { recognizeText(photoFile) }
            }
        )
    }

    /**
     * Sử dụng Google ML Kit Text Recognition để trích xuất chữ từ ảnh hóa đơn
     */
    private fun recognizeText(photoFile: java.io.File) {
        try {
            val image = InputImage.fromFilePath(this, android.net.Uri.fromFile(photoFile))
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Chuyển sang màn hình Thêm giao dịch kèm theo văn bản đã quét được
                    val intent = Intent(this, AddTransactionActivity::class.java)
                    intent.putExtra("EXTRA_IMAGE_PATH", photoFile.absolutePath)
                    intent.putExtra("EXTRA_OCR_TEXT", visionText.text)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Không nhận diện được hóa đơn.", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Log.e("OCR", "Error opening image file", e)
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

    override fun onPause() {
        super.onPause()
        // Lưu lại Tab hiện tại khi ứng dụng vào trạng thái Pause
        getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().putInt("last_nav_id", currentActiveNavId).apply()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
