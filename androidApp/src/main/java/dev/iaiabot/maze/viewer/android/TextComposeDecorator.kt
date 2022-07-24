package dev.iaiabot.maze.viewer.android

import dev.iaiabot.maze.entity.Cell
import dev.iaiabot.maze.entity.Status
import dev.iaiabot.maze.entity.decorator.Decorator
import kotlinx.coroutines.flow.MutableStateFlow

class TextComposeDecorator(
): Decorator {
    val procedures = MutableStateFlow<Cell?>(null)

    private var status: Status = Status.INIT

    override fun sequentialOutput(cell: Cell) {
        procedures.tryEmit(cell)
    }

    override fun onChangeStatus(status: Status) {
        this.status = status
    }
}
