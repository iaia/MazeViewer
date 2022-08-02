package dev.iaiabot.maze.viewer.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import dev.iaiabot.maze.entity.Cell

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val cellSize = 18

    val requireMazeWidth = (LocalConfiguration.current.screenWidthDp - 4) / cellSize
    val requireMazeHeight = (LocalConfiguration.current.screenHeightDp - (4 + 18)) / cellSize

    val cells by viewModel.cells.collectAsState()
    val generator by viewModel.selectedGenerator.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.start(requireMazeWidth, requireMazeHeight)
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
                Text(text = generator?.name ?: "")
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
                    key(cell?.x, cell?.y, cell?.javaClass, cell?.stepped) {
                        Cell(cell, cellSize)
                    }
                }
            }
        }
    }
}

val FIRST_STEPPED_COLOR = Color(25, 200, 240)
val SECOND_STEPPED_COLOR = Color(25, 150, 240)
val THIRD_STEPPED_COLOR = Color(25, 100, 240)
val GRATER_STEPPED_COLOR = Color(25, 50, 240)

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
                    is Cell.Floor -> Color.Green
                    is Cell.Stepped -> {
                        if (cell.origin is Cell.Start || cell.origin is Cell.Goal) {
                            Color.Red
                        } else {
                            when (cell.stepped) {
                                0 -> Color.Green
                                1 -> FIRST_STEPPED_COLOR
                                2 -> SECOND_STEPPED_COLOR
                                3 -> THIRD_STEPPED_COLOR
                                else -> GRATER_STEPPED_COLOR
                            }
                        }
                    }
                    is Cell.Shortest -> Color.Magenta
                    else -> Color.LightGray
                }
            )
    )
}
