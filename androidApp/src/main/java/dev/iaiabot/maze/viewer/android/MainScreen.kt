package dev.iaiabot.maze.viewer.android

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun MainScreen(
    viewModel: MainViewModel,
) {
    val procedures by viewModel.procedures.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.start()
    }

    MaterialTheme {

        Column {
            procedures.forEach { procedure ->
                Text(text = procedure)
            }
        }
    }
}
