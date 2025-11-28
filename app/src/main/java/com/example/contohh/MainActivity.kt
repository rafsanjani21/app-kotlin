package com.example.contohh

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.credentials.CredentialManager
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.contohh.auth.GoogleAuthUiClient
import com.example.contohh.data.remote.ApiClient
import com.example.contohh.data.remote.LoginRequest
import com.example.contohh.data.remote.RegisterRequest
import com.example.contohh.ui.CompleteProfileScreen
import com.example.contohh.ui.HomeScreen
import com.example.contohh.ui.LoginScreen
import com.example.contohh.ui.RegisterScreen
import com.example.contohh.ui.theme.ContohhTheme
import com.example.contohh.utils.DeviceInfo
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {

    private lateinit var googleAuth: GoogleAuthUiClient
    private lateinit var credentialManager: CredentialManager

    private var nav: androidx.navigation.NavHostController? = null
    private var lastFirebaseIdToken: String? = null
    private var isRegisterFlow: Boolean = false

    private val WEB_CLIENT_ID =
        "1085008448604-0oucanl872c1lkrovvsptl9k9jts7hsd.apps.googleusercontent.com"

    private val prefs by lazy {
        getSharedPreferences("auth_prefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        googleAuth = GoogleAuthUiClient(this)
        credentialManager = CredentialManager.create(this)

        setContent {
            val navController = rememberNavController()
            nav = navController

            ContohhTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    NavHost(navController, startDestination = "login") {

                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = { navController.navigate("home") },
                                onNavigateRegister = { navController.navigate("register") },
                                onGoogleLogin = {
                                    isRegisterFlow = false
                                    startGoogleSignIn()
                                }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = { navController.navigate("login") },
                                onNavigateLogin = { navController.popBackStack() },
                                onGoogleRegister = {
                                    isRegisterFlow = true
                                    startGoogleSignIn()
                                }
                            )
                        }

                        composable("complete_profile") {
                            CompleteProfileScreen(
                                onSubmit = {},
                                onSkip = { navController.navigate("home") }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                onLogoutSuccess = {
                                    logout()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // GOOGLE LOGIN (Credential Manager)
    private fun startGoogleSignIn() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = request
                )

                val credential = result.credential
                val googleData = GoogleIdTokenCredential.createFrom(credential.data)
                val googleToken = googleData.idToken

                if (googleToken == null) {
                    Log.e("GOOGLE_AUTH", "Google Token NULL")
                    return@launch
                }

                handleGoogleToken(googleToken)

            } catch (e: GetCredentialException) {
                Log.e("GOOGLE_AUTH", "Credential Error: ${e.message}")
            } catch (e: Exception) {
                Log.e("GOOGLE_AUTH", "ERROR: ${e.message}")
            }
        }
    }

    // LOGIN FIREBASE + KIRIM TOKEN KE BACKEND
    private fun handleGoogleToken(googleToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = googleAuth.signInWithToken(googleToken)
                val user = res.user ?: run {
                    Log.e("FB_AUTH", "User NULL")
                    return@launch
                }

                val firebaseIdToken = user.getIdToken(true).await().token ?: ""

                lastFirebaseIdToken = firebaseIdToken

                val device = DeviceInfo.getDeviceInfo()
                val deviceInfoString =
                    "Android ${device["os_version"]} (API ${device["api_level"]}); Brand=${device["device_brand"]}; Model=${device["device_model"]}"

                if (isRegisterFlow) {
                    val regBody = RegisterRequest(idToken = firebaseIdToken)
                    val regRes = ApiClient.api.registerGoogle(regBody)

                    if (!regRes.isSuccessful) {
                        Log.e("REGISTER", "Err ${regRes.code()} - ${regRes.errorBody()?.string()}")
                        return@launch
                    }

                    runOnUiThread { nav?.navigate("home") }
                    return@launch
                }

                val loginBody = LoginRequest(
                    idToken = firebaseIdToken,
                    deviceInfo = deviceInfoString
                )

                val loginRes = ApiClient.api.loginGoogle(loginBody)

                if (loginRes.isSuccessful) {
                    val jwt = loginRes.body()?.token
                    if (jwt != null) prefs.edit().putString("jwt_token", jwt).apply()

                    runOnUiThread { nav?.navigate("home") }
                    return@launch
                }

                val error = loginRes.errorBody()?.string()
                Log.e("LOGIN", "Error ${loginRes.code()} - $error")

                if (loginRes.code() == 400 && error?.contains("not registered") == true) {
                    runOnUiThread { nav?.navigate("register") }
                }

            } catch (e: Exception) {
                Log.e("FB_AUTH", "ERROR: ${e.message}")
            }
        }
    }

    // LOGOUT
    private fun logout() {
        val jwt = prefs.getString("jwt_token", null)

        CoroutineScope(Dispatchers.IO).launch {

            // Logout dari backend
            try {
                if (jwt != null) {
                    ApiClient.api.logout("Bearer $jwt")
                }
            } catch (_: Exception) {}

            // CLEAR JWT
            prefs.edit().clear().apply()

            // FIREBASE LOGOUT
            googleAuth.signOut()

            // Google Credential logout
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (_: Exception) {}

            runOnUiThread {
                nav?.navigate("login") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
}
