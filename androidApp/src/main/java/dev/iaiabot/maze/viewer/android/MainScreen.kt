package dev.iaiabot.maze.viewer.android

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.iaiabot.maze.entity.Cell
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun MainScreen(
    viewModel: MainViewModel,
) {
    val cells by viewModel.cells.collectAsState(emptyList())

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Cyan)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            cells.forEach { yCells ->
                Row {
                    yCells.forEach { cell ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .padding(2.dp)
                                .background(
                                    color = when (cell) {
                                        is Cell.Wall -> Color.Black
                                        is Cell.Start, is Cell.Goal, is Cell.Floor -> Color.Green
                                        null -> Color.DarkGray
                                    }
                                )
                        )
                    }
                }
            }
        }
    }
}
