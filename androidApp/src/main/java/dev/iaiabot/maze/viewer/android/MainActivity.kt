package dev.iaiabot.maze.viewer.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.iaiabot.maze.viewer.Greeting
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.LocalConfiguration

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {
                MainScreen(
                    viewModel = MainViewModel()
                )
            }
        }
    }
}
