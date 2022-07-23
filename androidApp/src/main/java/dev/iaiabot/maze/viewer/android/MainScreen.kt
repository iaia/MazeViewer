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
import dev.iaiabot.maze.entity.XY
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun MainScreen(
    viewModel: MainViewModel,
) {
    val cells by viewModel.procedures.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.start()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Cyan)
    ) {
        MazeCompose(width = viewModel.mazeWidth, height = viewModel.mazeHeight, cells = cells)
    }
}

@Composable
private fun MazeCompose(width: Int, height: Int, cells: Map<XY, Cell?>) {
    val displayCells: Array<Array<Cell?>> = Array(height) {
        Array(width) { null }
    }

    cells.keys.forEach {
        displayCells[it.y][it.x] = cells[it]
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        displayCells.forEach { column ->
            Row {
                column.forEach { cell ->
                    Cell(cell)
                }
            }
        }
    }
}


@Composable
private fun Cell(cell: Cell?) {
    if (cell == null) {
        return
    }
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
