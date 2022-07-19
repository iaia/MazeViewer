package dev.iaiabot.maze.viewer.android

import dev.iaiabot.maze.mazegenerator.decorator.StandardOutputDecorator
import dev.iaiabot.maze.mazegenerator.model.MazeImpl
import dev.iaiabot.maze.mazegenerator.strategy.DiggingGenerator

class MazeManager {

    fun main() {
        val maze = MazeImpl.generate(
            width = 7,
            height = 7,
            generator = DiggingGenerator(),
            decorator = StandardOutputDecorator(sequentialOutput = true),
        )
        maze.setup()
        maze.buildMap()
    }
}
