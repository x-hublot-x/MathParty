package com.example.project2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.project2.ui.navigation.MathPartyNavGraph
import com.example.project2.ui.theme.DeepObsidian
import com.example.project2.ui.theme.MathPartyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MathPartyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepObsidian
                ) {
                    MathPartyNavGraph()
                }
            }
        }
    }
}