package com.hendra.benerbenerrealalp.ui.routes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hendra.benerbenerrealalp.data.container.TokenManager
import com.hendra.benerbenerrealalp.ui.routes.Routes
import com.hendra.benerbenerrealalp.ui.view.LoginScreen
import com.hendra.benerbenerrealalp.ui.view.RegisterScreen
import com.hendra.benerbenerrealalp.ui.view.FinanceScreen
import com.hendra.benerbenerrealalp.ui.view.SleepScreen
import com.hendra.benerbenerrealalp.ui.view.TodoScreen

// --- Warna Tema Dashboard Sesuai Desain ---
private val BgDark = Color(0xFF1F1F1F) // Warna Latar Belakang Gelap
private val CardDark = Color(0xFF2C2C2E) // Warna Tombol Abu-abu Gelap
private val TextWhite = Color(0xFFFFFFFF) // Warna Teks Putih

@Composable
fun MomentumApp(
    startDestination: String
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = startDestination) {

        // --- 1. AUTH: LOGIN ---
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }

        // --- 2. AUTH: REGISTER ---
        composable(Routes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // --- 3. DASHBOARD HOME (Tampilan Baru) ---
        composable(Routes.Home.route) {
            // Gunakan Scaffold dengan warna latar belakang gelap
            Scaffold(containerColor = BgDark) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(24.dp), // Padding agar tidak terlalu mepet tepi
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center // Konten di tengah vertikal
                ) {
                    // Judul Dashboard
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(48.dp)) // Jarak yang cukup besar setelah judul

                    // Tombol Finance
                    DashboardButton(text = "Finance") {
                        navController.navigate(Routes.Finance.route)
                    }

                    Spacer(modifier = Modifier.height(16.dp)) // Jarak antar tombol

                    // Tombol To Do List
                    DashboardButton(text = "To Do List") {
                        navController.navigate(Routes.Todo.route)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tombol Alarm (Sleep Tracker)
                    DashboardButton(text = "Alarm") {
                        navController.navigate(Routes.Sleep.route)
                    }

                    Spacer(modifier = Modifier.height(48.dp)) // Jarak sebelum tombol logout

                    // Tombol Logout (Dibuat lebih simpel dengan TextButton)
                    TextButton(
                        onClick = {
                            TokenManager.clearToken(context)
                            navController.navigate(Routes.Login.route) {
                                popUpTo(Routes.Home.route) { inclusive = true }
                            }
                        }
                    ) {
                        Text("Logout", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            }
        }

        // --- FITUR LAINNYA ---
        composable(Routes.Finance.route) {
            FinanceScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Routes.Todo.route) {
            TodoScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Routes.Sleep.route) {
            SleepScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

// --- Komponen Tombol Dashboard Khusus ---
@Composable
fun DashboardButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp), // Tinggi tombol dibuat agak besar
        shape = RoundedCornerShape(12.dp), // Sudut membulat
        colors = ButtonDefaults.buttonColors(containerColor = CardDark), // Warna tombol gelap
        elevation = ButtonDefaults.buttonElevation(0.dp) // Hilangkan bayangan agar terlihat flat
    ) {
        // Box untuk mengatur teks agar rata kiri
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = text,
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}