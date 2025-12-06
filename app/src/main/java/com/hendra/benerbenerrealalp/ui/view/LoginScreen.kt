package com.hendra.benerbenerrealalp.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // PENTING: Import ini
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hendra.benerbenerrealalp.ui.theme.BenerbenerrealALPTheme
import com.hendra.benerbenerrealalp.ui.viewmodel.AuthUiState
import com.hendra.benerbenerrealalp.ui.viewmodel.AuthViewModel

// Warna sesuai desain gambar
private val BDark = Color(0xFF1F1F1F)
private val InputDark = Color(0xFF2C2C2E)
private val ButtonGrey = Color(0xFF616161)
private val TWhite = Color(0xFFFFFFFF)
private val TextBlue = Color(0xFF64B5F6)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    // (PERBAIKAN) Ambil Context untuk keperluan simpan Token
    val context = LocalContext.current

    // Reset state saat masuk layar
    LaunchedEffect(Unit) { viewModel.resetState() }

    // Handle Success
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) onLoginSuccess()
    }

    Scaffold(
        containerColor = BDark
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Judul
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge,
                color = TWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Input Email
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input Password
            CustomTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Login
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(color = TWhite)
            } else {
                Button(
                    onClick = {
                        // (PERBAIKAN) Kirim context ke fungsi login
                        viewModel.login(email, password, context)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp), // Pill shape
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGrey)
                ) {
                    Text("Login", color = TWhite, fontSize = 16.sp)
                }
            }

            // Error Message
            if (uiState is AuthUiState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (uiState as AuthUiState.Error).msg,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Link Register
            Row {
                Text("Belum punya akun? ", color = TWhite, fontSize = 12.sp)
                Text(
                    text = "Register",
                    color = TextBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}

// Komponen Input Custom agar sesuai desain
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputDark,
            unfocusedContainerColor = InputDark,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = TWhite,
            focusedTextColor = TWhite,
            unfocusedTextColor = TWhite
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

@Preview
@Composable
fun LoginPreview() {
    BenerbenerrealALPTheme {
        LoginScreen({}, {})
    }
}