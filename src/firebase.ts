import { initializeApp, getApps, getApp } from "firebase/app";
import {
  getAuth,
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
  signOut,
  updateProfile,
  sendPasswordResetEmail,
  onAuthStateChanged,
  User
} from "firebase/auth";

// Default Firebase project configuration
// Custom credentials can also be passed via environment variables
const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY || "AIzaSyDemoPiggyBiteFirebaseKey2026",
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN || "piggybite-app.firebaseapp.com",
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID || "piggybite-app",
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET || "piggybite-app.appspot.com",
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID || "987654321012",
  appId: import.meta.env.VITE_FIREBASE_APP_ID || "1:987654321012:web:a1b2c3d4e5f6g7h8"
};

// Initialize Firebase App instance safely
const app = !getApps().length ? initializeApp(firebaseConfig) : getApp();
export const auth = getAuth(app);

// Map Firebase Error Codes to Friendly Vietnamese Messages
export function getFirebaseErrorMessage(code: string): string {
  switch (code) {
    case "auth/email-already-in-use":
      return "Địa chỉ email này đã được sử dụng cho một tài khoản khác.";
    case "auth/invalid-email":
      return "Định dạng email không hợp lệ. Vui lòng kiểm tra lại.";
    case "auth/operation-not-allowed":
      return "Tính năng đăng nhập này chưa được kích hoạt trên Firebase.";
    case "auth/weak-password":
      return "Mật khẩu quá yếu. Vui lòng nhập ít nhất 6 ký tự.";
    case "auth/user-disabled":
      return "Tài khoản của bạn đã bị khóa hoặc vô hiệu hóa.";
    case "auth/user-not-found":
    case "auth/wrong-password":
    case "auth/invalid-credential":
      return "Email hoặc mật khẩu không chính xác.";
    case "auth/too-many-requests":
      return "Quá nhiều lần thử không thành công. Vui lòng thử lại sau ít phút.";
    case "auth/network-request-failed":
      return "Lỗi kết nối mạng. Vui lòng kiểm tra kết nối internet của bạn.";
    default:
      return "Đã xảy ra lỗi đăng nhập/đăng ký Firebase. Vui lòng thử lại.";
  }
}

// Register user with Email & Password
export async function registerWithFirebase(email: string, pass: string, name: string) {
  try {
    const userCredential = await createUserWithEmailAndPassword(auth, email, pass);
    if (name.trim()) {
      await updateProfile(userCredential.user, { displayName: name });
    }
    return { user: userCredential.user, error: null };
  } catch (err: any) {
    console.warn("Firebase Register Notice:", err.code || err.message);
    return { user: null, error: getFirebaseErrorMessage(err.code || "") };
  }
}

// Login user with Email & Password
export async function loginWithFirebase(email: string, pass: string) {
  try {
    const userCredential = await signInWithEmailAndPassword(auth, email, pass);
    return { user: userCredential.user, error: null };
  } catch (err: any) {
    console.warn("Firebase Login Notice:", err.code || err.message);
    return { user: null, error: getFirebaseErrorMessage(err.code || "") };
  }
}

// Send Password Reset Email
export async function resetFirebasePassword(email: string) {
  try {
    await sendPasswordResetEmail(auth, email);
    return { success: true, message: "Đã gửi email khôi phục mật khẩu. Vui lòng kiểm tra hòm thư của bạn!" };
  } catch (err: any) {
    return { success: false, message: getFirebaseErrorMessage(err.code || "") };
  }
}

// Logout user
export async function logoutFirebase() {
  try {
    await signOut(auth);
  } catch (e) {
    console.error("Firebase logout error:", e);
  }
}

// Subscribe to Auth status updates
export function listenToAuth(onChange: (user: User | null) => void) {
  return onAuthStateChanged(auth, onChange);
}
