package dev.iaiabot.maze.viewer.android

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.iaiabot.maze.entity.Cell
import dev.iaiabot.maze.entity.XY
import dev.iaiabot.maze.mazegenerator.model.MazeImpl
import dev.iaiabot.maze.mazegenerator.strategy.DiggingGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    val mazeWidth = 7
    val mazeHeight = 7
    val procedures = MutableStateFlow<Map<XY, Cell?>>(mapOf())
    private val decorator = TextComposeDecorator()

    init {
        viewModelScope.launch {
            decorator.procedures
                .collect { cell ->
                    if (cell == null) {
                        return@collect
                    }
                    val cells = procedures.value.toMutableMap()
                    cells[cell.xy] = cell
                    procedures.emit(cells)
                }
        }
    }

    suspend fun start() {
        delay(1000)
        val maze = MazeImpl.generate(
            width = mazeWidth,
            height = mazeHeight,
            generator = DiggingGenerator(),
            decorator = decorator,
        )
        maze.setup()
        maze.buildMap()
    }
}
