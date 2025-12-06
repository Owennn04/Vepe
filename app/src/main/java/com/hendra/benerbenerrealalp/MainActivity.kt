package com.hendra.benerbenerrealalp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hendra.benerbenerrealalp.data.container.TokenManager
import com.hendra.benerbenerrealalp.ui.routes.MomentumApp
import com.hendra.benerbenerrealalp.ui.routes.Routes
import com.hendra.benerbenerrealalp.ui.theme.BenerbenerrealALPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Cek apakah ada token di penyimpanan HP
        val token = TokenManager.getToken(applicationContext)

        // 2. Tentukan halaman awal
        // Jika token ada (tidak null) -> Langsung ke Home
        // Jika token tidak ada (null) -> Ke Login
        val startDestination = if (token != null) {
            Routes.Home.route
        } else {
            Routes.Login.route
        }

        setContent {
            BenerbenerrealALPTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Kirim tujuan awal ke MomentumApp
                    MomentumApp(startDestination = startDestination)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BenerbenerrealALPTheme {
        Greeting("Android")
    }
}