package com.money.app.ui;

/**
 * Main Entry activity of the application after authentication.
 * Handles primary navigation (Calendar, Home/Wallet, AI Camera) and global sync triggers.
 *
 * Architecture: AppCompatActivity with ViewBinding
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000~\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0005\u0018\u0000 82\u00020\u0001:\u00018B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0014J\b\u0010\u001f\u001a\u00020\u001cH\u0002J\b\u0010 \u001a\u00020\u001cH\u0002J\b\u0010!\u001a\u00020\u001cH\u0002J\b\u0010\"\u001a\u00020\u001cH\u0002J\u0010\u0010#\u001a\u00020\u001c2\u0006\u0010$\u001a\u00020\u000fH\u0002J\b\u0010%\u001a\u00020\u001cH\u0002J\b\u0010&\u001a\u00020\u001cH\u0002J\b\u0010\'\u001a\u00020\u001cH\u0002J\u0010\u0010(\u001a\u00020\u001c2\u0006\u0010)\u001a\u00020*H\u0002J\b\u0010+\u001a\u00020,H\u0002J\b\u0010-\u001a\u00020\u001cH\u0014J+\u0010.\u001a\u00020\u001c2\u0006\u0010/\u001a\u00020\u000f2\f\u00100\u001a\b\u0012\u0004\u0012\u000202012\u0006\u00103\u001a\u000204H\u0016\u00a2\u0006\u0002\u00105J\b\u00106\u001a\u00020\u001cH\u0014J\b\u00107\u001a\u00020\u001cH\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0017\u001a\u0010\u0012\f\u0012\n \u001a*\u0004\u0018\u00010\u00190\u00190\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00069"}, d2 = {"Lcom/money/app/ui/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "binding", "Lcom/money/app/databinding/ActivityMainBinding;", "cameraExecutor", "Ljava/util/concurrent/ExecutorService;", "imageCapture", "Landroidx/camera/core/ImageCapture;", "camera", "Landroidx/camera/core/Camera;", "cameraSelector", "Landroidx/camera/core/CameraSelector;", "flashMode", "", "currentZoom", "", "currentActiveNavId", "speechRecognizer", "Landroid/speech/SpeechRecognizer;", "auth", "Lcom/google/firebase/auth/FirebaseAuth;", "pickMedia", "Landroidx/activity/result/ActivityResultLauncher;", "Landroidx/activity/result/PickVisualMediaRequest;", "kotlin.jvm.PlatformType", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "showVoiceInputDialog", "setupZoomControls", "updateFlashIcon", "setupNavigation", "updateNavUI", "activeId", "startCamera", "stopCamera", "takePhotoAndRecognizeText", "recognizeText", "photoFile", "Ljava/io/File;", "allPermissionsGranted", "", "onDestroy", "onRequestPermissionsResult", "requestCode", "permissions", "", "", "grantResults", "", "(I[Ljava/lang/String;[I)V", "onPause", "onResume", "Companion", "app_debug"})
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
    private static final int REQUEST_CODE_PERMISSIONS = 10;
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
    
    private final void showVoiceInputDialog() {
    }
    
    private final void setupZoomControls() {
    }
    
    private final void updateFlashIcon() {
    }
    
    private final void setupNavigation() {
    }
    
    private final void updateNavUI(int activeId) {
    }
    
    private final void startCamera() {
    }
    
    private final void stopCamera() {
    }
    
    private final void takePhotoAndRecognizeText() {
    }
    
    private final void recognizeText(java.io.File photoFile) {
    }
    
    private final boolean allPermissionsGranted() {
        return false;
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    @java.lang.Override()
    public void onRequestPermissionsResult(int requestCode, @org.jetbrains.annotations.NotNull()
    java.lang.String[] permissions, @org.jetbrains.annotations.NotNull()
    int[] grantResults) {
    }
    
    @java.lang.Override()
    protected void onPause() {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\t\u00a8\u0006\n"}, d2 = {"Lcom/money/app/ui/MainActivity$Companion;", "", "<init>", "()V", "REQUEST_CODE_PERMISSIONS", "", "REQUIRED_PERMISSIONS", "", "", "[Ljava/lang/String;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}