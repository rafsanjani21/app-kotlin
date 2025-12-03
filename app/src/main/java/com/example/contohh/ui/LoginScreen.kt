package com.example.contohh.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.contohh.R

@Composable
fun LoginScreen(
    onNavigateRegister: () -> Unit,
    onGoogleLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }



    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0A1A2F),
                            Color(0xFF123860),
                            Color(0xFF1E6BAF)
                        )
                    )
                )
                .blur(30.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Login",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 50.sp
            )

            Spacer(Modifier.height(24.dp))


            Button(
                onClick = { onGoogleLogin() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_android_black_24dp),
                    tint = Color.Unspecified,
                    contentDescription = null
                )
                Spacer(Modifier.width(12.dp))
                Text("Login dengan Google", color = Color.Black)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Belum punya akun? Register",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onNavigateRegister() },
                color = Color.LightGray,
                textDecoration = TextDecoration.Underline,
            )
        }
    }
}
