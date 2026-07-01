package com.money.app.ui;

/**
 * Hoạt động chính (Main Activity): Màn hình chính sau khi đăng nhập.
 * Đóng vai trò là trung tâm điều hướng và xử lý các tính năng thông minh:
 * - Điều hướng giữa các Fragment: Lịch (Calendar), Trang chủ (Home), Camera AI.
 * - Tích hợp CameraX để quét hóa đơn và nhận diện văn bản (OCR).
 * - Nhận diện giọng nói để thêm nhanh giao dịch.
 * - Kích hoạt đồng bộ dữ liệu Firebase khi khởi chạy ứng dụng.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\u0018\u0000 72\u00020\u0001:\u00017B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0014J\b\u0010\u001f\u001a\u00020\u001cH\u0002J\b\u0010 \u001a\u00020\u001cH\u0002J\b\u0010!\u001a\u00020\u001cH\u0002J\b\u0010\"\u001a\u00020\u001cH\u0002J\b\u0010#\u001a\u00020\u001cH\u0002J\b\u0010$\u001a\u00020\u001cH\u0002J\b\u0010%\u001a\u00020\u001cH\u0002J\b\u0010&\u001a\u00020\u001cH\u0002J\u0018\u0010\'\u001a\u00020\u001c2\u0006\u0010(\u001a\u00020\u000f2\u0006\u0010)\u001a\u00020*H\u0002J\u0010\u0010+\u001a\u00020\u001c2\u0006\u0010,\u001a\u00020\u000fH\u0002J\b\u0010-\u001a\u00020\u001cH\u0002J\b\u0010.\u001a\u00020\u001cH\u0002J\b\u0010/\u001a\u00020\u001cH\u0002J\u0010\u00100\u001a\u00020\u001c2\u0006\u00101\u001a\u000202H\u0002J\b\u00103\u001a\u000204H\u0002J\b\u00105\u001a\u00020\u001cH\u0014J\b\u00106\u001a\u00020\u001cH\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0017\u001a\u0010\u0012\f\u0012\n \u001a*\u0004\u0018\u00010\u00190\u00190\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00068"}, d2 = {"Lcom/money/app/ui/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "binding", "Lcom/money/app/databinding/ActivityMainBinding;", "cameraExecutor", "Ljava/util/concurrent/ExecutorService;", "imageCapture", "Landroidx/camera/core/ImageCapture;", "camera", "Landroidx/camera/core/Camera;", "cameraSelector", "Landroidx/camera/core/CameraSelector;", "flashMode", "", "currentZoom", "", "currentActiveNavId", "speechRecognizer", "Landroid/speech/SpeechRecognizer;", "auth", "Lcom/google/firebase/auth/FirebaseAuth;", "pickMedia", "Landroidx/activity/result/ActivityResultLauncher;", "Landroidx/activity/result/PickVisualMediaRequest;", "kotlin.jvm.PlatformType", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "restoreLastFragment", "showVoiceInputDialog", "checkAudioPermissionAndShowVoiceDialog", "flipCamera", "toggleFlash", "setupZoomControls", "updateFlashIcon", "setupNavigation", "switchFragment", "id", "fragment", "Landroidx/fragment/app/Fragment;", "updateNavUI", "activeId", "startCamera", "stopCamera", "takePhotoAndRecognizeText", "recognizeText", "photoFile", "Ljava/io/File;", "allPermissionsGranted", "", "onDestroy", "onPause", "Companion", "app_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.money.app.databinding.ActivityMainBinding binding;
    private java.util.concurrent.ExecutorService cameraExecutor;
    @org.jetbrains.annotations.Nullable()
    private androidx.camera.core.ImageCapture imageCapture;
    @org.jetbrains.annotations.Nullable()
    private androidx.camera.core.Camera camera;
    @org.jetbrains.annotations.NotNull()
    private androidx.camera.core.CameraSelector cameraSelector;
    private int flashMode = androidx.camera.core.ImageCapture.FLASH_MODE_OFF;
    private float currentZoom = 1.0F;
    private int currentActiveNavId = com.money.app.R.id.btnNavHome;
    @org.jetbrains.annotations.Nullable()
    private android.speech.SpeechRecognizer speechRecognizer;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.auth.FirebaseAuth auth = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<androidx.activity.result.PickVisualMediaRequest> pickMedia = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String[] REQUIRED_PERMISSIONS = {"android.permission.CAMERA"};
    @org.jetbrains.annotations.NotNull()
    public static final com.money.app.ui.MainActivity.Companion Companion = null;
    
    public MainActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    /**
     * Khôi phục Fragment gần nhất mà người dùng sử dụng
     */
    private final void restoreLastFragment() {
    }
    
    /**
     * Hiển thị BottomSheet ghi âm và xử lý nhận diện giọng nói tiếng Việt
     */
    private final void showVoiceInputDialog() {
    }
    
    private final void checkAudioPermissionAndShowVoiceDialog() {
    }
    
    private final void flipCamera() {
    }
    
    private final void toggleFlash() {
    }
    
    private final void setupZoomControls() {
    }
    
    private final void updateFlashIcon() {
    }
    
    /**
     * Cài đặt logic chuyển đổi giữa các màn hình chính (Navigation)
     */
    private final void setupNavigation() {
    }
    
    private final void switchFragment(int id, androidx.fragment.app.Fragment fragment) {
    }
    
    private final void updateNavUI(int activeId) {
    }
    
    /**
     * Khởi tạo luồng Camera bằng CameraX
     */
    private final void startCamera() {
    }
    
    private final void stopCamera() {
    }
    
    /**
     * Chụp ảnh hóa đơn và gửi đi nhận diện văn bản (OCR)
     */
    private final void takePhotoAndRecognizeText() {
    }
    
    /**
     * Sử dụng Google ML Kit Text Recognition để trích xuất chữ từ ảnh hóa đơn
     */
    private final void recognizeText(java.io.File photoFile) {
    }
    
    private final boolean allPermissionsGranted() {
        return false;
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    @java.lang.Override()
    protected void onPause() {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0016\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lcom/money/app/ui/MainActivity$Companion;", "", "<init>", "()V", "REQUIRED_PERMISSIONS", "", "", "[Ljava/lang/String;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}