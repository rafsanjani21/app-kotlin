package com.example.contohh.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import com.example.contohh.R


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateRegister: () -> Unit,
    onGoogleLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false)}

    //Error Message
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    //Validation func
    fun validate(): Boolean {
        var valid = true

        //Empty Email
        if(email.isEmpty()) {
            emailError = "Email must not be empty!"
            valid = false
        }

        //Wrong Email
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Email format is not valid!"
            valid = false
        } else {
            emailError = ""
        }

        //Empty Password
        if (password.isEmpty()) {
            passwordError = "Password must not be empty!"
            valid = false
        }

        //minimum character password 6
        else if (password.length<6) {
            passwordError = "Minimum character 6"
            valid = false
        } else {
            passwordError = ""
        }

        return valid
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
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

            //EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError=""},
                label = { Text(
                    emailError.ifEmpty { "Email" },
                    color = if (emailError.isNotEmpty()) Red else Color.White
                )   },
                leadingIcon = {
                    Icon(Icons.Rounded.AccountCircle,
                        contentDescription = "",
                        tint = Color.White)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = if (emailError.isNotEmpty()) Red else Color.LightGray,
                    unfocusedBorderColor = if (emailError.isNotEmpty()) Red else Color.LightGray,
                    unfocusedContainerColor = Color(0x33ffffff),
                    focusedContainerColor = Color(0x33ffffff)
                )
            )

            Spacer(Modifier.height(16.dp))

            //PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = ""},
                label = { Text(
                    passwordError.ifEmpty { "Password" },
                    color = if (passwordError.isNotEmpty()) Red else Color.White) },
                leadingIcon = {
                    Icon(Icons.Rounded.Lock,
                        contentDescription = "",
                        tint = Color.White)
                },
                isError = passwordError.isNotEmpty(),
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
                            .clickable{passwordVisible = !passwordVisible},
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors= OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = if(passwordError.isNotEmpty()) Red else Color.LightGray,
                    unfocusedBorderColor = if(passwordError.isNotEmpty()) Red else Color.LightGray,
                    unfocusedContainerColor = Color(0x33ffffff),
                    focusedContainerColor = Color(0x33ffffff)
                )
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (validate()) {
                        onLoginSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0FA6DC)
                )
            ) {
                Text("Login")
            }

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


