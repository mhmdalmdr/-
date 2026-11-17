package com.example.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "GoogleAuthHelper"

data class StudentAccount(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String?
)

sealed interface AuthResult {
    data class Success(val account: StudentAccount) : AuthResult
    data object Cancelled : AuthResult
    data class Error(val message: String) : AuthResult
}

fun FirebaseUser.toStudentAccount(): StudentAccount {
    return StudentAccount(
        uid = uid,
        displayName = displayName?.takeIf { it.isNotBlank() } ?: "طالب الشهادة السودانية",
        email = email.orEmpty(),
        photoUrl = photoUrl?.toString()
    )
}

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

object GoogleAuthHelper {

    fun safeAuthOrNull(context: Context): FirebaseAuth? {
        return try {
            if (FirebaseApp.getApps(context.applicationContext).isEmpty()) {
                FirebaseApp.initializeApp(context.applicationContext)
            }
            if (FirebaseApp.getApps(context.applicationContext).isNotEmpty()) {
                Firebase.auth
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseAuth unavailable in current environment", e)
            null
        }
    }

    fun observeAuthState(context: Context): Flow<StudentAccount?> {
        val auth = safeAuthOrNull(context) ?: return flowOf(null)
        return callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                trySend(firebaseAuth.currentUser?.toStudentAccount())
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }
    }

    suspend fun attemptAutoSignIn(context: Context) {
        val auth = safeAuthOrNull(context) ?: return
        if (auth.currentUser != null) return
        val activity = context.findActivity() ?: return

        try {
            val clientId = activity.getString(R.string.default_web_client_id)
            if (clientId.isBlank()) return

            val credentialManager = CredentialManager.create(activity)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                auth.signInWithCredential(firebaseCredential).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: NoCredentialException) {
            Log.d(TAG, "No previously authorized Google account found for silent sign-in")
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "Silent Google sign-in cancelled")
        } catch (e: Exception) {
            Log.w(TAG, "Silent Google auto sign-in skipped", e)
        }
    }

    suspend fun signInWithGoogle(context: Context): AuthResult {
        val auth = safeAuthOrNull(context)
            ?: return AuthResult.Error("تعذر تهيئة خدمة المصادقة من Firebase. يرجى التحقق من الاتصال والمحاولة لاحقاً.")
        val activity = context.findActivity()
            ?: return AuthResult.Error("تعذر فتح نافذة تسجيل الدخول من هذا السياق.")

        return try {
            val clientId = activity.getString(R.string.default_web_client_id)
            val credentialManager = CredentialManager.create(activity)
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
                serverClientId = clientId
            ).build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                val user = authResult.user
                    ?: return AuthResult.Error("لم يتم استلام بيانات حساب الطالب من Google.")
                AuthResult.Success(user.toStudentAccount())
            } else {
                AuthResult.Error("نوع بيانات الاعتماد المستلم غير مدعوم.")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "Google sign-in cancelled by user", e)
            AuthResult.Cancelled
        } catch (e: NoCredentialException) {
            Log.w(TAG, "No Google credentials available on device", e)
            AuthResult.Error("لم يتم العثور على حساب Google مضاف في الجهاز. يرجى إضافة حساب Google في إعدادات الجهاز والمحاولة مجدداً.")
        } catch (e: GetCredentialException) {
            Log.e(TAG, "CredentialManager error during Google sign-in", e)
            AuthResult.Error(e.localizedMessage ?: "تعذر إتمام تسجيل الدخول بحساب Google.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase Google authentication failed", e)
            AuthResult.Error(e.localizedMessage ?: "حدث خطأ أثناء ربط حساب Google.")
        }
    }

    suspend fun signOut(context: Context) {
        withContext(NonCancellable) {
            try {
                CredentialManager.create(context.applicationContext)
                    .clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w(TAG, "Failed to clear Credential Manager state on sign-out", e)
            }
            safeAuthOrNull(context)?.signOut()
        }
    }
}
