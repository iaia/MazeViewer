package dev.iaiabot.maze.viewer.android

import dev.iaiabot.maze.entity.Cell
import dev.iaiabot.maze.entity.Status
import dev.iaiabot.maze.entity.decorator.Decorator
import kotlinx.coroutines.flow.MutableStateFlow

class TextComposeDecorator(
): Decorator {
    val procedures = MutableStateFlow<Cell?>(null)
    val batchProcedure = MutableStateFlow<List<List<Cell?>>>(emptyList())

    override fun onChangeBuildStatus(status: Status, cells: Collection<Collection<Cell>>) {
        when (status) {
            Status.FINISH_SETUP,
            Status.FINISH_BUILD -> {
                batchProcedure.tryEmit(cells.map { it.toList() })
            }
            else -> {}
        }
    }

    override fun onChangeResolveStatus(status: Status, cells: Collection<Cell>) {
    }

    override fun outputSequentialBuilding(cell: Cell) {
        procedures.tryEmit(cell)
    }

    override fun outputSequentialResolving(procedures: Collection<Cell>) {
        // procedures.tryEmit(cell)
    }
}
