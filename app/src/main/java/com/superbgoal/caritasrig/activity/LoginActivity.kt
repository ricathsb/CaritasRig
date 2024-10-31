package com.superbgoal.caritasrig.activity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.security.MessageDigest
import java.util.UUID

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val emailFromSignUp = intent.getStringExtra("email") ?: ""
        setContent {
            CaritasRigTheme {
                Scaffold {
                    LoginScreen(
                        modifier = Modifier.padding(it),
                        initialEmail = emailFromSignUp
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    initialEmail: String = ""
) {
    var email by remember {
        mutableStateOf(initialEmail)
    }
    var password by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val autheticationManager = remember {
        AuthenticationManager(context)
    }
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = modifier
        .fillMaxSize()
        .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)


    ) {
        Text(
            text="Sign-in",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text="Please fill in your email and password",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "Email")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.Email, contentDescription = null)
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()

        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = "Password")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.Lock, contentDescription = null)
            },
            visualTransformation = PasswordVisualTransformation(),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
    )

        Spacer(modifier = Modifier.height(20.dp))

        Button (onClick ={
            if(password.isBlank() and email.isBlank()){
                Toast.makeText(context, "Email and password cannot be blank", Toast.LENGTH_SHORT).show()
                return@Button
            }
            if (email.isBlank()) {
                Toast.makeText(context, "Email cannot be blank", Toast.LENGTH_SHORT).show()
                return@Button
            }
            if (password.isBlank()) {
                Toast.makeText(context, "Password cannot be blank", Toast.LENGTH_SHORT).show()
                return@Button
            }
            if (password.length < 6) {
                Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@Button
            }

            autheticationManager.loginWithEmail(email, password)
                .onEach { authResponse ->
                    when (authResponse) {
                        is AuthResponse.Success -> {
                            val intent = Intent(context, HomeActivity::class.java)
                            context.startActivity(intent)
                        }
                        is AuthResponse.Error -> {
                            if (authResponse.message == "Please verify your email before logging in.") {
                                Toast.makeText(context, "Please verify your email to proceed.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Your data is incorrect", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .launchIn(coroutineScope)
        },
            modifier = Modifier.fillMaxWidth()){
            Text(
                text = "Sign in" ,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "or continue with")
        }

        OutlinedButton(
            onClick = {
                autheticationManager.signInWithGoogle()
                    .onEach {
                        if (it is AuthResponse.Success) {
                            Log.d("tesLogin", "Login...")
                            val intent = Intent(context, HomeActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                    .launchIn(coroutineScope)
            },
            modifier = Modifier.fillMaxWidth()
        ){
            Image(
                painter = painterResource(id = R.drawable.logogoogle),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )

            Text(text = "Sign in with Google",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,)
        }
        OutlinedButton(
            onClick = {
                val intent = Intent(context, SignUpActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign up")
        }
        OutlinedButton(
            onClick = {
                if (email.isBlank()) {
                    Toast.makeText(context, "Please enter your email to reset password", Toast.LENGTH_SHORT).show()
                } else {
                    autheticationManager.resetPassword(email)
                        .onEach { authResponse ->
                            when (authResponse) {
                                is AuthResponse.Success -> {
                                    Toast.makeText(context, "Password reset link has been sent to your email.", Toast.LENGTH_SHORT).show()
                                }
                                is AuthResponse.Error -> {
                                    Toast.makeText(context, "Failed to send reset link: ${authResponse.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .launchIn(coroutineScope)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Forgot Password?")
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun Loginpreview(){
    CaritasRigTheme {
        LoginScreen()
    }
}

//back-end login

class AuthenticationManager(val context: Context) {
    private val auth = FirebaseAuth.getInstance()

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
                        trySend(AuthResponse.Success)
                    } else{
                        trySend(AuthResponse.Error("Please verify your email before logging in."))
                        auth.signOut()
                    }
                } else {
                    trySend(AuthResponse.Error(task.exception?.message ?: "Unknown Error"))
                }
            }
        awaitClose()
    }

    fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md =MessageDigest.getInstance("SHA-256")
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
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    trySend(AuthResponse.Success)
                                } else {
                                    trySend(
                                        AuthResponse.Error(
                                            it.exception?.message ?: "Unknown Error"
                                        )
                                    )
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
