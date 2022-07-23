package dev.iaiabot.maze.viewer.android

import dev.iaiabot.maze.entity.Cell
import dev.iaiabot.maze.entity.decorator.Decorator
import kotlinx.coroutines.flow.MutableStateFlow

class TextComposeDecorator(
): Decorator {
    val procedures = MutableStateFlow<Cell?>(null)

    override fun sequentialOutput(cell: Cell) {
        procedures.tryEmit(cell)
    }
}
