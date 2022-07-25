package dev.iaiabot.maze.viewer.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.iaiabot.maze.entity.Cell
import dev.iaiabot.maze.entity.Generator
import dev.iaiabot.maze.entity.Maze
import dev.iaiabot.maze.entity.Player
import dev.iaiabot.maze.mazegenerator.strategy.DiggingGenerator
import dev.iaiabot.maze.mazeresolver.strategy.RightHandResolver
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel: ViewModel() {
    val cells = MutableStateFlow<List<List<Cell?>>>(emptyList())
    val selectedGenerator = MutableStateFlow<Generator?>(null)

    private val generators = listOf(
        DiggingGenerator(priority = DiggingGenerator.Priority.DEPTH_FIRST),
        DiggingGenerator(priority = DiggingGenerator.Priority.BREADTH_FIRST),
        DiggingGenerator(priority = DiggingGenerator.Priority.RANDOM),
        // LayPillarGenerator(),
        // WallExtendGenerator(),
    )
    private val resolvers = listOf(
        RightHandResolver(),
    )
    private val decorator: TextComposeDecorator = TextComposeDecorator()
    private val maze = Maze(
        decorator = decorator,
    )
    private val resolver = resolvers.random(Random(System.currentTimeMillis()))
    private val player = Player(maze, resolver, decorator)

    init {
        viewModelScope.launch {
            decorator.procedures
                .buffer(Channel.UNLIMITED)
                .collect { procedure ->
                    val cell = procedure ?: return@collect
                    val cells = cells.value.toMutableList()
                    cells[cell.y] = cells[cell.y].toMutableList().also {
                        it[cell.x] = cell
                    }
                    delay(10)
                    this@MainViewModel.cells.emit(cells)
                }
        }
    }

    suspend fun start(requireMazeWidth: Int, requireMazeHeight: Int) {
        repeat(10) {
            val generator = generators.random(Random(System.currentTimeMillis()))
            selectedGenerator.tryEmit(generator)
            val mazeWidthHeight = decideMazeWidthHeight(requireMazeWidth, requireMazeHeight)
            cells.tryEmit(
                List(mazeWidthHeight.second) {
                    List(mazeWidthHeight.first) { null }
                }
            )

            maze.setup(
                width = mazeWidthHeight.first,
                height = mazeWidthHeight.second,
                generator = generator,
            )
            maze.buildMap()
            // player.start()
            delay(10000)
        }
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
