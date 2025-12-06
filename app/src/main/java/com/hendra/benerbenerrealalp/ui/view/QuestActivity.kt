package com.hendra.benerbenerrealalp.ui.view

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hendra.benerbenerrealalp.service.AlarmSoundService
import com.hendra.benerbenerrealalp.ui.theme.BenerbenerrealALPTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class QuestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        showOnLockScreen()
        super.onCreate(savedInstanceState)

        setContent {
            BenerbenerrealALPTheme {
                QuestScreenUI(
                    onSuccess = {
                        stopService(Intent(this, AlarmSoundService::class.java))
                        finish()
                    }
                )
            }
        }
    }

    private fun showOnLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }
}

// --- UI (Stateless) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestScreenUI(onSuccess: () -> Unit) {
    val num1 = remember { Random.nextInt(10, 50) }
    val num2 = remember { Random.nextInt(5, 20) }
    val correctAnswer = num1 + num2

    var answer by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val timeString = SimpleDateFormat("h:mm", Locale.getDefault()).format(calendar.time)
    val amPm = SimpleDateFormat("a", Locale.getDefault()).format(calendar.time)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    val (greeting, icon, temp) = when (hour) {
        in 5..11 -> Triple("GOOD MORNING", Icons.Default.Cloud, "23°C")
        in 12..17 -> Triple("GOOD AFTERNOON", Icons.Default.WbSunny, "30°C")
        else -> Triple("GOOD NIGHT", Icons.Default.NightsStay, "21°C")
    }

    // Blokir Tombol Back
    BackHandler {}

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1C1C1E) // Dark Grey Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = greeting,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Jam Besar
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = timeString,
                        color = Color.White,
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Thin
                    )
                    Text(
                        text = amPm,
                        color = Color.Gray,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cuaca Dummy
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if(hour in 12..17) Color(0xFFFFD54F) else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = temp,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }

            // Quest
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$num1 + $num2 =",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = answer,
                    onValueChange = {
                        answer = it
                        error = false
                    },
                    placeholder = { Text("Answer...", color = Color.Gray) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2C2C2E),
                        unfocusedContainerColor = Color(0xFF2C2C2E),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (error) {
                    Text(
                        text = "Incorrect, try again!",
                        color = Color(0xFFEF5350),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (answer == correctAnswer.toString()) {
                            onSuccess()
                        } else {
                            error = true
                            answer = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E8E93))
                ) {
                    Text("Submit", fontSize = 18.sp, color = Color.Black)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun QuestScreenPreview() {
    BenerbenerrealALPTheme {
        QuestScreenUI(onSuccess = {})
    }
}