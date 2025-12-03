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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.example.contohh.R

@Composable
fun RegisterScreen(
    infoMessage: String? = null,
    onNavigateLogin: () -> Unit,
    onGoogleRegister: () -> Unit
) {

    var error by remember { mutableStateOf("") }


    Box(modifier = Modifier.fillMaxSize()) {

        // Background gradient blur
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFF0000),
                            Color(0xFFB30000),
                            Color(0xFF7A0000)
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
                "Register",
                color = Color.White,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(24.dp))

            val showInfo = remember(infoMessage) { infoMessage ?: "" }

            if (showInfo.isNotEmpty()) {
                Text(
                    text = showInfo,
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(12.dp))
            }


            if (error.isNotEmpty()) {
                Text(error, color = Color.Red)
                Spacer(Modifier.height(8.dp))
            }


            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { onGoogleRegister() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_android_black_24dp),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(12.dp))
                Text("Daftar dengan Google", color = Color.Black)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Sudah punya akun? Login",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onNavigateLogin() },
                color = Color.LightGray,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}
