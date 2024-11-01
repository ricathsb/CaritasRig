package com.superbgoal.caritasrig.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.HomeActivity
import com.superbgoal.caritasrig.activity.RegisterActivity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest
import java.util.UUID

class AuthenticationManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()

    private fun checkUserInDatabase(context: Context, userId: String) {
        val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
        val database = FirebaseDatabase.getInstance(databaseUrl).reference

        database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
            // Log snapshot details for debugging
            Log.d("checkUserInDatabase", "Snapshot value: ${snapshot.value}")
            Log.d("checkUserInDatabase", "Snapshot exists: ${snapshot.exists()}")

            // Check if the snapshot exists and is not null
            if (snapshot.exists() && snapshot.value != null) {
                Log.d("checkUserInDatabase", "User exists in database, redirecting to RegisterActivity")
                Intent(context, HomeActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(it)
                }
            } else {
                Log.d("checkUserInDatabase", "User does not exist in database, redirecting to RegisterActivity")
                Intent(context, RegisterActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    it.putExtra("userId", userId)
                    context.startActivity(it)
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("checkUserInDatabase", "Error checking user in database: ${exception.message}")
        }
    }



    fun resetPassword(email: String): Flow<AuthResponse> = callbackFlow {
        // Mengirim email reset password
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { resetTask ->
                if (resetTask.isSuccessful) {
                    // Email reset berhasil dikirim
                    trySend(AuthResponse.Success)
                } else {
                    // Jika terjadi kesalahan, kirimkan error yang sesuai
                    if (resetTask.exception is FirebaseAuthInvalidUserException) {
                        // Jika email tidak terdaftar
                        trySend(AuthResponse.Error("Email tidak terdaftar"))
                    } else {
                        // Mengirimkan error umum
                        trySend(AuthResponse.Error(resetTask.exception?.message ?: "Error sending reset email"))
                    }
                }
            }
        awaitClose()
    }


    fun loginWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        Log.d("login", "success")
                        trySend(AuthResponse.Success)
                    } else{
                        trySend(AuthResponse.Error("Please verify your email before logging in."))
                        auth.signOut()
                    }
                } else {
                    Log.d("login", "error")
                    trySend(AuthResponse.Error(task.exception?.message ?: "Unknown Error"))
                }
            }
        awaitClose()
    }

    private fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.fold("") { str, it ->
            str + "%02x".format(it)
        }
    }

    fun signInWithGoogle(): Flow<AuthResponse> = callbackFlow  {
        Log.d("signinwithgoogle", "signInWithGoogle:")
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_client_id))
            .setAutoSelectEnabled(false)
            .setNonce(createNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = result.credential
            Log.d("credential", credential.toString())
            if (credential is CustomCredential) {
                if (credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleidTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        val firebaseCredential = GoogleAuthProvider
                            .getCredential(
                                googleidTokenCredential.idToken,
                                null
                            )
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = auth.currentUser?.uid ?: ""
                                    checkUserInDatabase(context,userId)
                                } else {
                                    trySend(AuthResponse.Error(task.exception?.message ?: "Unknown Error"))
                                }
                            }

                    } catch (e: GoogleIdTokenParsingException) {
                        trySend(AuthResponse.Error(e.localizedMessage ?: "Unknown Error"))
                    }
                }
            }


        } catch (e: Exception) {
            Log.d("error", e.toString())
            trySend(AuthResponse.Error(e.localizedMessage ?: "Unknown Error"))
        }
        awaitClose()
    }
}

sealed interface AuthResponse {
    data object Success : AuthResponse
    data class Error(val message: String) : AuthResponse
}