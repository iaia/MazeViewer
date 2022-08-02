package dev.iaiabot.maze.viewer.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.iaiabot.maze.entity.*
import dev.iaiabot.maze.mazegenerator.strategy.DiggingGenerator
import dev.iaiabot.maze.mazegenerator.strategy.LayPillarGenerator
import dev.iaiabot.maze.mazegenerator.strategy.WallExtendGenerator
import dev.iaiabot.maze.mazeresolver.strategy.RightHandResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel: ViewModel() {
    val cells = MutableStateFlow<List<List<Cell>>>(emptyList())
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
    private val decorator: MazeCallback = MazeCallback()
    private val maze = Maze(
        decorator = decorator,
        dispatcher = Dispatchers.Main,
    )
    private val resolver = resolvers.random(Random(System.currentTimeMillis()))
    private val player = Player(
        maze = maze,
        resolver = resolver,
        decorator = decorator,
        dispatcher = Dispatchers.Main
    )
    private var mazeWidthHeight = Pair(0, 0)

    init {
        viewModelScope.launch {
            decorator.batchProcedure
                .collect { procedure ->
                    this@MainViewModel.cells.emit(procedure)
                }
        }

        viewModelScope.launch {
            decorator.status
                .buffer(Channel.RENDEZVOUS)
                .collect { status ->
                    when (status) {
                        Status.FINISH_SETUP -> maze.buildMap()
                        Status.FINISH_BUILD -> {
                            delay(3000)
                            player.start()
                        }
                        Status.FINISH_RESOLVE -> {
                            delay(3000)
                            player.findShortestPath()
                        }
                        Status.FINISH_FIND_SHORTEST_PATH -> {
                            delay(5000)
                            start(mazeWidthHeight.first, mazeWidthHeight.second)
                        }
                        else -> {}
                    }
                }
        }

        viewModelScope.launch {
            merge(decorator.buildProcedure, decorator.resolveProcedure)
                .buffer(Channel.UNLIMITED)
                .collect { procedure ->
                    procedure ?: return@collect
                    val cells = cells.value.toMutableList()
                    cells[procedure.y] = cells[procedure.y].toMutableList().also {
                        it[procedure.x] = procedure
                    }
                    delay(1)
                    this@MainViewModel.cells.emit(cells)
                }
        }
    }

    fun start(requireMazeWidth: Int, requireMazeHeight: Int) {
        mazeWidthHeight = decideMazeWidthHeight(requireMazeWidth, requireMazeHeight)
        cells.tryEmit(
            List(mazeWidthHeight.second) { y ->
                List(mazeWidthHeight.first) { x -> Cell.Empty(XY(x, y)) }
            }
        )
        val generator = generators.random(Random(System.currentTimeMillis()))
        selectedGenerator.tryEmit(generator)

        maze.setup(
            width = mazeWidthHeight.first,
            height = mazeWidthHeight.second,
            generator = generator,
        )
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
