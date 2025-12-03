package com.example.contohh

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.credentials.CredentialManager
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.GetCredentialRequest
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

    private var nav: NavHostController? = null

    private var lastFirebaseIdToken: String? = null
    private var isRegisterFlow = false

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

                        // LOGIN
                        composable("login") {
                            LoginScreen(
                                onNavigateRegister = { navController.navigate("register") },
                                onGoogleLogin = {
                                    isRegisterFlow = false
                                    startGoogleSignIn()
                                }
                            )
                        }

                        // REGISTER
                        composable("register") {
                            val infoMessage =
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.get<String>("info") ?: ""

                            RegisterScreen(
                                infoMessage = infoMessage,
                                onNavigateLogin = { navController.popBackStack() },
                                onGoogleRegister = {
                                    isRegisterFlow = true
                                    startGoogleSignIn()
                                }
                            )
                        }

                        // COMPLETE PROFILE
                        composable("complete_profile") {
                            CompleteProfileScreen(
                                onSubmit = { name ->
                                    sendManualNameToBackend(name)
                                }
                            )
                        }

                        // HOME
                        composable("home") {
                            HomeScreen(onLogoutSuccess = { logout() })
                        }
                    }
                }
            }
        }
    }

    // ============================================================
    // GOOGLE SIGN-IN
    // ============================================================
    private fun startGoogleSignIn() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val googleOpt = GetGoogleIdOption.Builder()
                    .setServerClientId(WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleOpt)
                    .build()

                val result = credentialManager.getCredential(this@MainActivity, request)
                val googleData = GoogleIdTokenCredential.createFrom(result.credential.data)
                val googleToken = googleData.idToken ?: return@launch

                handleGoogleToken(googleToken)

            } catch (e: Exception) {
                Log.e("GOOGLE_AUTH", "ERROR: ${e.message}")
            }
        }
    }

    // ============================================================
    // HANDLE GOOGLE TOKEN
    // ============================================================
    private fun handleGoogleToken(googleToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingUser = googleAuth.currentUser()

                val firebaseIdToken = if (existingUser != null) {
                    existingUser.getIdToken(false).await().token ?: ""
                } else {
                    val result = googleAuth.signInWithToken(googleToken)
                    val user = result.user ?: return@launch
                    user.getIdToken(true).await().token ?: ""
                }

                if (firebaseIdToken.isEmpty()) return@launch
                lastFirebaseIdToken = firebaseIdToken

                val device = DeviceInfo.getDeviceInfo()
                val deviceInfo =
                    "Android ${device["os_version"]} (API ${device["api_level"]}); " +
                            "Brand=${device["device_brand"]}; Model=${device["device_model"]}"

                // LOGIN BACKEND
                val loginBody = LoginRequest(firebaseIdToken, deviceInfo)
                val loginRes = ApiClient.api.loginGoogle(loginBody)

                if (loginRes.isSuccessful) {
                    val jwt = loginRes.body()?.token
                    if (jwt != null) prefs.edit().putString("jwt_token", jwt).apply()

                    runOnUiThread {
                        nav?.navigate("home")
                    }
                    return@launch
                }

                val error = loginRes.errorBody()?.string() ?: ""

                if (error.contains("user not registered")) {
                    if (isRegisterFlow) {
                        runOnUiThread { nav?.navigate("complete_profile") }
                    } else {
                        runOnUiThread {
                            nav?.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("info", "Anda belum terdaftar. Silakan registrasi dulu.")
                            nav?.navigate("register")
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("HANDLE_TOKEN", "ERR: ${e.message}")
            }
        }
    }

    // ============================================================
    // REGISTER GOOGLE → PAKAI NAMA MANUAL
    // ============================================================
    private fun sendManualNameToBackend(name: String, loginSource: String = "android") {
        val idToken = lastFirebaseIdToken ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = ApiClient.api.registerGoogle(RegisterRequest(idToken, name, loginSource)) // Menambahkan loginSource

                // Bila user sudah ada → tetap login
                if (!res.isSuccessful) {
                    val err = res.errorBody()?.string() ?: ""

                    if (err.contains("already registered")) {
                        loginAfterRegister(idToken, loginSource) // Menambahkan loginSource
                        return@launch
                    }

                    Log.e("REGISTER", "Err: $err")
                    return@launch
                }

                // Register sukses → login
                loginAfterRegister(idToken, loginSource) // Menambahkan loginSource

            } catch (e: Exception) {
                Log.e("REGISTER", "ERROR: ${e.message}")
            }
        }
    }


    // ============================================================
    // AFTER REGISTER → LOGIN
    // ============================================================
    private fun loginAfterRegister(idToken: String, loginSource: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val device = DeviceInfo.getDeviceInfo()
                val deviceInfo =
                    "Android ${device["os_version"]} (API ${device["api_level"]}); Brand=${device["device_brand"]}"

                val loginRes = ApiClient.api.loginGoogle(LoginRequest(idToken, deviceInfo))

                if (!loginRes.isSuccessful) return@launch

                val jwt = loginRes.body()?.token
                if (jwt != null) prefs.edit().putString("jwt_token", jwt).apply()

                runOnUiThread {
                    nav?.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }

            } catch (e: Exception) {
                Log.e("LOGIN_AFTER_REG", "ERROR: ${e.message}")
            }
        }
    }


    // ============================================================
    // LOGOUT
    // ============================================================
    private fun logout() {
        val jwt = prefs.getString("jwt_token", null)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (jwt != null) ApiClient.api.logout("Bearer $jwt")
            } catch (_: Exception) {}

            prefs.edit().clear().apply()
            googleAuth.signOut()

            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (_: Exception) {}

            runOnUiThread {
                nav?.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }
    }
}
