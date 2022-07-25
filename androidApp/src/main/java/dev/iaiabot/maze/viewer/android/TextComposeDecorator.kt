package dev.iaiabot.maze.viewer.android

import dev.iaiabot.maze.entity.Cell
import dev.iaiabot.maze.entity.Status
import dev.iaiabot.maze.entity.decorator.Decorator
import kotlinx.coroutines.flow.MutableStateFlow

class TextComposeDecorator(
): Decorator {
    val procedures = MutableStateFlow<Cell?>(null)
    val batchProcedure = MutableStateFlow<Array<Array<Cell?>>>(emptyArray())

    private var status: Status = Status.INIT

    override fun sequentialOutput(cell: Cell) {
        when (status) {
            Status.BUILDING -> procedures.tryEmit(cell)
            else -> {}
        }
    }

    override fun onChangeStatus(status: Status, cells: Array<Array<Cell?>>) {
        this.status = status
    }
}
