package dev.iaiabot.maze.viewer.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.iaiabot.maze.entity.*
import dev.iaiabot.maze.mazegenerator.strategy.DiggingGenerator
import dev.iaiabot.maze.mazegenerator.strategy.LayPillarGenerator
import dev.iaiabot.maze.mazegenerator.strategy.WallExtendGenerator
import dev.iaiabot.maze.mazeresolver.strategy.RightHandResolver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
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
    private val resolvers = listOf(
        RightHandResolver(),
    )

    init {
        viewModelScope.launch {
            decorator.procedures
                .buffer(capacity = 1000000)
                .collect { procedure ->
                    val cell = procedure.first ?: return@collect
                    val cells = cells.value.toMutableList()
                    cells[cell.y] = cells[cell.y].toMutableList().also {
                        it[cell.x] = cell
                    }
                    when (procedure.second) {
                        Status.SETUP -> { }
                        else -> delay(1)
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
        val maze = Maze(
            width = mazeWidthHeight.first,
            height = mazeWidthHeight.second,
            generator = generator,
            decorator = decorator,
        )
        maze.setup()
        maze.buildMap()

        val resolver = resolvers.random()
        val player = Player(maze, resolver, decorator)
        player.start()
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
