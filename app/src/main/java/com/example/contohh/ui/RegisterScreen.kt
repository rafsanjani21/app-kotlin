package com.example.contohh.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.example.contohh.R

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateLogin: () -> Unit,
    onGoogleRegister: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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
                "Register",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 50.sp
            )

            Spacer(Modifier.height(24.dp))

            // NAME
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Lengkap",
                    color = Color.White,
                    )},

                leadingIcon = {
                    Icon(Icons.Rounded.Person,
                        contentDescription = "",
                        tint = Color.White)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    unfocusedContainerColor = Color(0x33ffffff),
                    focusedContainerColor = Color(0x33ffffff)
                )
            )

            Spacer(Modifier.height(16.dp))

            // EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email",
                        color = Color.White )},
                leadingIcon = {
                    Icon(Icons.Rounded.AccountCircle,
                        contentDescription = "",
                        tint = Color.White)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    unfocusedContainerColor = Color(0x33ffffff),
                    focusedContainerColor = Color(0x33ffffff)
                )
            )

            Spacer(Modifier.height(16.dp))

            // PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password",
                    color = Color.White) },
                leadingIcon = {
                    Icon(Icons.Rounded.Lock,
                        contentDescription = "",
                        tint = Color.White)
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        painterResource(id = R.drawable.visible)
                    else painterResource(id = R.drawable.visibility)

                    Icon(
                        painter = image,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { passwordVisible = !passwordVisible },
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    unfocusedContainerColor = Color(0x33ffffff),
                    focusedContainerColor = Color(0x33ffffff)
                )
            )

            Spacer(Modifier.height(24.dp))

            // REGISTER MANUAL
            Button(
                onClick = { onRegisterSuccess() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0FA6DC)
                )
            ) {
                Text("Register")
            }

            Spacer(Modifier.height(12.dp))

            // 🔥 REGISTER DENGAN GOOGLE
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
                textDecoration = TextDecoration.Underline,
            )
        }
    }
}

