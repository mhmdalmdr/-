package com.example.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

object FirestoreErrorHandler {

    fun handleFirestoreError(
        exception: Throwable,
        operationType: OperationType,
        path: String?,
        auth: FirebaseAuth? = null
    ): String {
        val causes = generateSequence(exception) { it.cause }
        val rootError = causes.firstOrNull { it is FirebaseFirestoreException }
            ?: causes.firstOrNull { it !is CancellationException }
            ?: exception

        val currentUser = try {
            (auth ?: FirebaseAuth.getInstance()).currentUser
        } catch (_: Exception) {
            null
        }

        val providerInfoList = currentUser?.providerData?.map { provider ->
            JSONObject().apply {
                put("providerId", provider.providerId)
                put("email", provider.email)
            }
        } ?: emptyList()

        val authInfoJson = JSONObject().apply {
            put("userId", currentUser?.uid)
            put("email", currentUser?.email)
            put("emailVerified", currentUser?.isEmailVerified)
            put("tenantId", currentUser?.tenantId)
            put("providerInfo", JSONArray(providerInfoList))
        }

        val errorInfoJson = JSONObject().apply {
            put("error", rootError.message ?: rootError.toString())
            put("operationType", operationType.value)
            put("path", path)
            put("authInfo", authInfoJson)
        }

        val jsonString = errorInfoJson.toString()
        Log.e("FirestoreError", "Firestore Error: $jsonString")
        return jsonString
    }

    fun toFriendlyArabicMessage(error: Throwable): String {
        val firestoreException = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()

        return when (firestoreException?.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                "تعذر إتمام العملية بسبب صلاحيات الوصول. تأكد من تسجيل الدخول بحساب Google وصحة البيانات المدخلة."
            FirebaseFirestoreException.Code.UNAVAILABLE,
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                "الاتصال بخدمة المنتدى متذبذب حالياً. يرجى التحقق من الإنترنت والمحاولة مرة أخرى."
            FirebaseFirestoreException.Code.UNAUTHENTICATED ->
                "انتهت جلسة الدخول. يرجى تسجيل الدخول بحساب Google للمتابعة."
            FirebaseFirestoreException.Code.NOT_FOUND ->
                "لم يعد هذا المنشور أو الرد متوفراً في المنتدى."
            else -> error.localizedMessage?.takeIf { it.isNotBlank() }
                ?: "حدث خطأ غير متوقع أثناء الاتصال بالمنتدى الطلابي."
        }
    }
}
