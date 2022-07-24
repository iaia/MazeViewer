package dev.iaiabot.maze.viewer.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.iaiabot.maze.entity.Cell

@Composable
fun MainScreen(
    viewModel: MainViewModel,
) {
    val cellSize = 18

    val requireMazeWidth = (LocalConfiguration.current.screenWidthDp - 4) / cellSize
    val requireMazeHeight = (LocalConfiguration.current.screenHeightDp - (4 + 48)) / cellSize

    val cells by viewModel.cells.collectAsState()
    val generator by viewModel.selectedGenerator.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.start(requireMazeWidth, requireMazeHeight)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = { viewModel.start(requireMazeWidth, requireMazeHeight) }) {
                    Text(text = "Regenerate")

                }
                Text(text = generator?.javaClass?.simpleName.toString())
            }

            MazeCompose(
                cellSize = cellSize.dp,
                cells = cells,
            )
        }
    }
}

@Composable
private fun MazeCompose(
    cellSize: Dp,
    cells: List<List<Cell?>>,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        cells.forEach { column ->
            Row {
                column.forEach { cell ->
                    Cell(cell, cellSize)
                }
            }
        }
    }
}

@Composable
private fun Cell(
    cell: Cell?,
    cellSize: Dp,
) {
    Box(
        modifier = Modifier
            .size(cellSize)
            .padding(1.dp)
            .background(
                color = when (cell) {
                    is Cell.Wall -> Color.Black
                    is Cell.Start, is Cell.Goal -> Color.Red
                    is Cell.Floor -> {
                        if (cell.stepped == 0) {
                            Color.Green
                        } else {
                            val alpha = (256 - (cell.stepped * 64)) / 256F

                            Color.Blue.copy(
                                alpha = if (alpha < 0) {
                                    0F
                                } else {
                                    alpha
                                }
                            )
                        }
                    }
                    null -> Color.LightGray
                }
            )
    )
}
