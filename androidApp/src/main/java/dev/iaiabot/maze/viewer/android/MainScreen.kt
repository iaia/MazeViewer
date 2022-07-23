package dev.iaiabot.maze.viewer.android

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val screenWidth = LocalConfiguration.current.screenWidthDp

    val cells by viewModel.procedures.collectAsState()
    val generator by viewModel.selectedGenerator.collectAsState()
    val mazeWidthHeight by viewModel.mazeWidthHeight.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.start(screenWidth, screenHeight)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = { viewModel.start(screenWidth, screenHeight) }) {
                    Text(text = "Regenerate")

                }
                Text(text = generator?.javaClass?.simpleName.toString())
            }

            MazeCompose(width = mazeWidthHeight.first, height = mazeWidthHeight.second, cells = cells)
        }
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
                    is Cell.Start, is Cell.Goal -> Color.Red
                    is Cell.Floor -> Color.Green
                    null -> Color.DarkGray
                }
            )
    )
}
