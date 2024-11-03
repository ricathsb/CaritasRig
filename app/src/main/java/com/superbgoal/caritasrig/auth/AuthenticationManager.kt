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
import com.superbgoal.caritasrig.activity.LoginActivity
import com.superbgoal.caritasrig.activity.RegisterActivity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest
import java.util.UUID

class AuthenticationManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()

    fun checkUserInDatabase(userId: String) {
        val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
        val database = FirebaseDatabase.getInstance(databaseUrl).reference
        val currentUser = auth.currentUser

        currentUser?.reload()?.addOnCompleteListener { reloadTask ->
            if (reloadTask.isSuccessful) {
                val imageUrl = currentUser.photoUrl?.toString()

                // Langkah pertama: cek apakah user ada di database
                database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
                    Log.d("checkUserInDatabase", "Snapshot value: ${snapshot.value}")
                    Log.d("checkUserInDatabase", "Snapshot exists: ${snapshot.exists()}")

                    if (snapshot.exists() && snapshot.value != null) {
                        // User ada di database, cek apakah emailnya terverifikasi
                        if (currentUser.isEmailVerified) {
                            Log.d("checkUserInDatabase", "User exists and email is verified, redirecting to HomeActivity")
                            Intent(context, HomeActivity::class.java).also {
                                context.startActivity(it)
                            }
                        } else {
                            Log.d("checkUserInDatabase", "User exists but email not verified, redirecting to LoginActivity")
                            Intent(context, LoginActivity::class.java).also {
                                context.startActivity(it)
                            }
                        }
                    } else {
                        // User tidak ada di database, arahkan ke RegisterActivity
                        Log.d("checkUserInDatabase", "User does not exist in database, redirecting to RegisterActivity")
                        Intent(context, RegisterActivity::class.java).also {
                            it.putExtra("userId", userId)
                            it.putExtra("imageUrl", imageUrl)
                            Log.d("checkUserInDatabase", "imageUrl: $imageUrl")
                            context.startActivity(it)
                        }
                    }
                }.addOnFailureListener { exception ->
                    Log.e("checkUserInDatabase", "Error checking user in database: ${exception.message}")
                }
            } else {
                Log.e("checkUserInDatabase", "Failed to reload user: ${reloadTask.exception?.message}")
            }
        }
    }







    fun resetPassword(email: String): Flow<AuthResponse> = callbackFlow {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { resetTask ->
                if (resetTask.isSuccessful) {
                    trySend(AuthResponse.Success)
                } else {
                    if (resetTask.exception is FirebaseAuthInvalidUserException) {
                        trySend(AuthResponse.Error("Email tidak terdaftar"))
                    } else {
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
                                    checkUserInDatabase(userId)
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


