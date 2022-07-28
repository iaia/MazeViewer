package dev.iaiabot.maze.viewer.android

import dev.iaiabot.maze.entity.Cell
import dev.iaiabot.maze.entity.Status
import dev.iaiabot.maze.entity.decorator.Decorator
import kotlinx.coroutines.flow.MutableStateFlow

class TextComposeDecorator(
): Decorator {
    val buildProcedure = MutableStateFlow<Cell?>(null)
    val batchProcedure = MutableStateFlow<List<List<Cell>>>(emptyList())
    val resolveProcedure = MutableStateFlow<Cell?>(null)
    val status = MutableStateFlow<Status>(Status.INIT)
    private var _status = Status.INIT

    override fun onChangeBuildStatus(status: Status, cells: Collection<Collection<Cell>>) {
        onChangeStatus(status)
        when (status) {
            Status.FINISH_SETUP -> {
                batchProcedure.tryEmit(cells.map { it.toList() })
                this.status.tryEmit(status)
            }
            Status.FINISH_BUILD -> this.status.tryEmit(status)
            else -> {}
        }
    }

    override fun onChangeResolveStatus(status: Status, cells: Collection<Cell>) {
        onChangeStatus(status)
        when (status) {
            Status.FINISH_RESOLVE -> this.status.tryEmit(status)
            else -> {}
        }
    }

    override fun outputSequentialBuilding(cell: Cell) {
        when (_status) {
            Status.BUILDING -> buildProcedure.tryEmit(cell)
            else -> {}
        }
    }

    override fun outputSequentialResolving(procedures: Collection<Cell>) {
        resolveProcedure.tryEmit(procedures.lastOrNull())
    }

    private fun onChangeStatus(status: Status) {
        _status = status
    }
}
