package dev.iaiabot.maze.viewer.android

import dev.iaiabot.maze.entity.Cell
import dev.iaiabot.maze.entity.Status
import dev.iaiabot.maze.entity.decorator.Decorator
import kotlinx.coroutines.flow.MutableStateFlow

class TextComposeDecorator(
): Decorator {
    val procedures = MutableStateFlow<Pair<Cell?, Status>>(Pair(null, Status.INIT))

    override fun sequentialOutput(cell: Cell, status: Status) {
        procedures.tryEmit(Pair(cell, status))
    }
}
