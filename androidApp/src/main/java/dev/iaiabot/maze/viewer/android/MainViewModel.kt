package dev.iaiabot.maze.viewer.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.iaiabot.maze.entity.Cell
import dev.iaiabot.maze.entity.Generator
import dev.iaiabot.maze.entity.Maze
import dev.iaiabot.maze.entity.Player
import dev.iaiabot.maze.mazegenerator.strategy.DiggingGenerator
import dev.iaiabot.maze.mazegenerator.strategy.LayPillarGenerator
import dev.iaiabot.maze.mazegenerator.strategy.WallExtendGenerator
import dev.iaiabot.maze.mazeresolver.strategy.RightHandResolver
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel: ViewModel() {
    val cells = MutableStateFlow<List<List<Cell?>>>(emptyList())
    val selectedGenerator = MutableStateFlow<Generator?>(null)

    private val generators = listOf(
        DiggingGenerator(priority = DiggingGenerator.Priority.DEPTH_FIRST),
        DiggingGenerator(priority = DiggingGenerator.Priority.BREADTH_FIRST),
        DiggingGenerator(priority = DiggingGenerator.Priority.RANDOM),
        LayPillarGenerator(),
        WallExtendGenerator(),
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
            merge(decorator.procedures, decorator.batchProcedure)
                .buffer(Channel.UNLIMITED)
                .collect { procedure ->
                    when (procedure) {
                        is Cell -> {
                            val cells = cells.value.toMutableList()
                            cells[procedure.y] = cells[procedure.y].toMutableList().also {
                                it[procedure.x] = procedure
                            }
                            delay(1)
                            this@MainViewModel.cells.emit(cells)
                        }
                        is List<*> -> {
                            this@MainViewModel.cells.emit(procedure as List<List<Cell?>>)
                        }
                    }
                }
        }
    }

    suspend fun start(requireMazeWidth: Int, requireMazeHeight: Int) {
        val mazeWidthHeight = decideMazeWidthHeight(requireMazeWidth, requireMazeHeight)
        cells.tryEmit(
            List(mazeWidthHeight.second) {
                List(mazeWidthHeight.first) { null }
            }
        )

        repeat(10) {
            val generator = generators.random(Random(System.currentTimeMillis()))
            selectedGenerator.tryEmit(generator)

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
