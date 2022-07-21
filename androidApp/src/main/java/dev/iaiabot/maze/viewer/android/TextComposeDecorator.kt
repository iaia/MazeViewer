package dev.iaiabot.maze.viewer.android

import android.util.Log
import androidx.compose.runtime.produceState
import dev.iaiabot.maze.entity.Cell
import dev.iaiabot.maze.mazegenerator.decorator.Decorator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class TextComposeDecorator(
): Decorator {
    val procedures = MutableStateFlow<Cell?>(null)

    override fun fullOutput(cells: Array<Array<Cell?>>) {
    }

    override fun sequentialOutput(cell: Cell) {
        procedures.tryEmit(cell)
    }
}
