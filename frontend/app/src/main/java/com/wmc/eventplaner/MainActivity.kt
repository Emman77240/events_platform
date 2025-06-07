package com.wmc.eventplaner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wmc.eventplaner.ui.theme.EventPlanerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            enableEdgeToEdge()
            EventPlanerTheme () {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyApp(innerPadding)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    EventPlanerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MyApp()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {


}