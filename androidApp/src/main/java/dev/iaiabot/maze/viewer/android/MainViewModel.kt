package dev.iaiabot.maze.viewer.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.iaiabot.maze.entity.Cell
import dev.iaiabot.maze.mazegenerator.Generator
import dev.iaiabot.maze.mazegenerator.model.MazeImpl
import dev.iaiabot.maze.mazegenerator.strategy.DiggingGenerator
import dev.iaiabot.maze.mazegenerator.strategy.LayPillarGenerator
import dev.iaiabot.maze.mazegenerator.strategy.WallExtendGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    val mazeWidthHeight = MutableStateFlow(Pair(5, 5))
    val cells = MutableStateFlow<List<List<Cell?>>>(emptyList())
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
                    val cells = cells.value.toMutableList()
                    cells[cell.y] = cells[cell.y].toMutableList().also {
                        it[cell.x] = cell
                    }
                    this@MainViewModel.cells.emit(cells)
                }
        }
    }

    fun start(requireMazeWidth: Int, requireMazeHeight: Int) {
        val generator = generators.random()
        selectedGenerator.tryEmit(generator)
        val mazeWidthHeight = decideMazeWidthHeight(requireMazeWidth, requireMazeHeight)
        this.mazeWidthHeight.tryEmit(mazeWidthHeight)
        this.cells.tryEmit(
            List(mazeWidthHeight.second) {
                List(mazeWidthHeight.first) { null }
            }
        )
        val maze = MazeImpl(
            width = mazeWidthHeight.first,
            height = mazeWidthHeight.second,
            generator = generator,
            decorator = decorator,
        )
        maze.setup()
        maze.buildMap()
    }

    private fun decideMazeWidthHeight(mazeWidth: Int, mazeHeight: Int): Pair<Int, Int> {
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
