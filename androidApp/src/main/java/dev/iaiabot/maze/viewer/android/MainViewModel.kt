package dev.iaiabot.maze.viewer.android

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.iaiabot.maze.entity.Cell
import dev.iaiabot.maze.entity.XY
import dev.iaiabot.maze.mazegenerator.Generator
import dev.iaiabot.maze.mazegenerator.model.MazeImpl
import dev.iaiabot.maze.mazegenerator.strategy.DiggingGenerator
import dev.iaiabot.maze.mazegenerator.strategy.LayPillarGenerator
import dev.iaiabot.maze.mazegenerator.strategy.WallExtendGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    val mazeWidth = 9
    val mazeHeight = 9
    val procedures = MutableStateFlow<Map<XY, Cell?>>(mapOf())
    val selectedGenerator = MutableStateFlow<Generator?>(null)

    private val decorator = TextComposeDecorator()
    private val generators = listOf(
        DiggingGenerator(),
        LayPillarGenerator(),
        WallExtendGenerator(),
    )

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

    fun start() {
        val generator = generators.random()
        selectedGenerator.tryEmit(generator)
        val maze = MazeImpl.generate(
            width = mazeWidth,
            height = mazeHeight,
            generator = generator,
            decorator = decorator,
        )
        maze.setup()
        maze.buildMap()
    }
}
