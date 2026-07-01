package com.money.app.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.money.app.R

/**
 * Màn hình Bản đồ (Map): Sử dụng WebView để hiển thị Google Maps.
 * Tính năng chính:
 * - Tự động tìm kiếm các cây ATM và ngân hàng gần vị trí hiện tại của người dùng.
 * - Hỗ trợ xin quyền truy cập vị trí (GPS) để bản đồ hiển thị chính xác.
 * - Xử lý chuyển hướng từ Web sang ứng dụng bản đồ gốc (Google Maps App) nếu cần.
 */
class MapActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        findViewById<View>(R.id.btnClose).setOnClickListener {
            finish()
        }

        val webView = findViewById<WebView>(R.id.webView)
        
        setupWebView(webView) // Cấu hình WebView
        
        checkLocationPermission() // Kiểm tra quyền truy cập vị trí
    }

    /**
     * Cấu hình các thông số cho WebView để chạy Google Maps mượt mà.
     */
    private fun setupWebView(webView: WebView) {
        webView.webViewClient = object : WebViewClient() {
            /**
             * Xử lý các liên kết đặc biệt (như intent:// hoặc maps:) để mở ứng dụng Google Maps nếu máy có cài đặt.
             */
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                
                if (url.startsWith("intent://") || url.startsWith("maps:")) {
                    try {
                        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        if (intent != null) {
                            val info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                            if (info != null) {
                                startActivity(intent) // Mở ứng dụng Maps gốc
                                return true
                            } else {
                                // Nếu không có ứng dụng Maps, thử mở link fallback trong trình duyệt
                                val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                                if (fallbackUrl != null) {
                                    view?.loadUrl(fallbackUrl)
                                    return true
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return true
                }
                return false
            }
        }
        
        // Cấp quyền lấy vị trí cho WebView
        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                callback?.invoke(origin, true, false)
            }
        }

        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true // Cho phép chạy Javascript
        settings.domStorageEnabled = true // Cho phép lưu trữ DOM (quan trọng cho Google Maps)
        settings.allowFileAccess = true
        settings.setGeolocationEnabled(true) // Bật GPS cho Web
        
        // Giả lập User Agent của trình duyệt Chrome di động để tránh bị chặn
        settings.userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36"
        
        // Tải trang tìm kiếm ATM/Ngân hàng gần đây
        webView.loadUrl("https://www.google.com/maps/search/ATM+Bank+near+me/")
    }

    /**
     * Yêu cầu người dùng cấp quyền truy cập vị trí
     */
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Nếu đã được cấp quyền, tải lại bản đồ để lấy vị trí mới nhất
                findViewById<WebView>(R.id.webView).reload()
            }
        }
    }
}
