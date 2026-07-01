package com.money.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.money.app.R

/**
 * Màn hình Giới thiệu (Intro): Màn hình đầu tiên người dùng nhìn thấy khi mới cài đặt ứng dụng.
 * Sử dụng Jetpack Compose để xây dựng giao diện hiện đại.
 * Chỉ hiển thị một lần duy nhất nhờ lưu trạng thái vào SharedPreferences.
 */
class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        com.money.app.util.ThemeHelper.applyTheme(this)
        super.onCreate(savedInstanceState)
        
        // Kiểm tra xem người dùng đã xem màn hình giới thiệu chưa
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("intro_shown", false)) {
            // Nếu đã xem rồi, chuyển thẳng đến màn hình Đăng nhập
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Thiết lập nội dung giao diện bằng Compose
        setContent {
            IntroScreen {
                // Khi nhấn nút "Bắt đầu", đánh dấu đã xem và chuyển màn hình
                prefs.edit().putBoolean("intro_shown", true).apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}

/**
 * Giao diện màn hình giới thiệu được viết bằng Jetpack Compose
 */
@Composable
fun IntroScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4A5BCC)) // Màu nền chủ đạo của PiggyBite
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo ứng dụng
        Image(
            painter = painterResource(id = R.drawable.icon_app_money),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Tiêu đề chào mừng
        Text(
            text = "Chào mừng bạn đến với\nPiggyBite",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Mô tả ngắn gọn về ứng dụng
        Text(
            text = "Quản lý tài chính thông minh, theo dõi chi tiêu và tiết kiệm cùng bạn bè trong thời gian thực.",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Nút bấm bắt đầu
        Button(
            onClick = onStart,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text(text = "Bắt đầu ngay", color = Color(0xFF4A5BCC), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
