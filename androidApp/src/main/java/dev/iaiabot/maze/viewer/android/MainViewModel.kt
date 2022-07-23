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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    val mazeWidthHeight = MutableStateFlow(Pair(5, 5))
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

    fun start(screenWidthDp: Int, screenHeightDp: Int) {
        val generator = generators.random()
        selectedGenerator.tryEmit(generator)
        val mazeWidthHeight = decideMazeWidthHeight(screenWidthDp, screenHeightDp)
        this.mazeWidthHeight.tryEmit(mazeWidthHeight)
        val maze = MazeImpl.generate(
            width = mazeWidthHeight.first,
            height = mazeWidthHeight.second,
            generator = generator,
            decorator = decorator,
        )
        maze.setup()
        maze.buildMap()
    }

    private fun decideMazeWidthHeight(screenWidthDp: Int, screenHeightDp: Int): Pair<Int, Int> {
        val mazeWidth = (screenWidthDp - 20) / 48
        val mazeHeight = (screenHeightDp - 320) / 48

        return Pair(
            if (mazeWidth % 2 == 0) {
                mazeWidth - 1
            } else {
                mazeWidth
            },
            if (mazeHeight % 2 == 0) {
                mazeHeight - 1
            } else {
                mazeHeight
            }
        )
    }
}
