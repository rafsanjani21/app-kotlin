package com.example.contohh.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CompleteProfileScreen(
    initialName: String? = null,
    onSubmit: (String) -> Unit,
    onSkip: () -> Unit
) {
    var name by remember { mutableStateOf(initialName.orEmpty()) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lengkapi Nama",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { onSubmit(name) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan")
            }

            TextButton(
                onClick = { onSkip() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lewati")
            }
        }
    }
}
