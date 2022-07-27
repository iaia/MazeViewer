package dev.iaiabot.maze.viewer.android

import dev.iaiabot.maze.entity.Cell
import dev.iaiabot.maze.entity.Status
import dev.iaiabot.maze.entity.decorator.Decorator
import kotlinx.coroutines.flow.MutableStateFlow

class TextComposeDecorator(
): Decorator {
    val buildProcedure = MutableStateFlow<Cell?>(null)
    val batchProcedure = MutableStateFlow<List<List<Cell>>>(emptyList())
    val resolveProcedure = MutableStateFlow<List<Cell>>(emptyList())

    override fun onChangeBuildStatus(status: Status, cells: Collection<Collection<Cell>>) {
        when (status) {
            Status.FINISH_SETUP -> {
                batchProcedure.tryEmit(cells.map { it.toList() })
            }
            else -> {}
        }
    }

    override fun onChangeResolveStatus(status: Status, cells: Collection<Cell>) {
        resolveProcedure.tryEmit(cells.toList())
    }

    override fun outputSequentialBuilding(cell: Cell) {
        buildProcedure.tryEmit(cell)
    }

    override fun outputSequentialResolving(procedures: Collection<Cell>) {
        // procedures.tryEmit(cell)
    }
}
