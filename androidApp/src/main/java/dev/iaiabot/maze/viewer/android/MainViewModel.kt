package dev.iaiabot.maze.viewer.android

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.iaiabot.maze.mazegenerator.model.MazeImpl
import dev.iaiabot.maze.mazegenerator.strategy.DiggingGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    val mazeWidth = 7
    val mazeHeight = 7
    val procedures = MutableStateFlow<List<String>>(emptyList())
    private val decorator = TextComposeDecorator()

    init {
        viewModelScope.launch {
            decorator.procedures
                .collect {
                    procedures.emit(procedures.value + it)
                }
        }
    }

    suspend fun start() {
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
