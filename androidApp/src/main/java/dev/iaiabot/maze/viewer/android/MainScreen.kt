package dev.iaiabot.maze.viewer.android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    }
}
