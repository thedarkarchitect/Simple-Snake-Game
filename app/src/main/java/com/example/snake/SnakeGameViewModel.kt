package com.example.snake

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SnakeGameViewModel : ViewModel() {

    private val _state = MutableStateFlow(SnakeGameState())
    val state = _state.asStateFlow()

    fun onEvent(event: SnakeGameEvent) {
        when(event) {
            SnakeGameEvent.PauseGame -> {
                _state.update { it.copy(gameState = GameState.PAUSED) }
            }
            SnakeGameEvent.ResetGame -> { _state.value = SnakeGameState() }
            SnakeGameEvent.StartGame -> {
                _state.update { it.copy(gameState = GameState.STARTED) }
                viewModelScope.launch {
                    while (state.value.gameState == GameState.STARTED) {
                        val delayMillis = when(state.value.snake.size) {
                            in 1..5 -> 120L
                            in 6..10 -> 110L
                            else -> 100L
                        }
                        delay(delayMillis)
                        _state.value = updateGame(state.value)
                    }
                }
            }
            is SnakeGameEvent.UpdateDirection -> updateDirection(event.offset, event.canvasWidth)
        }
    }

    private fun updateDirection(offset: Offset, canvasWidth: Int) {
        if (!state.value.isGameOver) {
            val cellSize = canvasWidth / state.value.xAxisGridSize
            val tapX = (offset.x / cellSize).toInt()
            val tapY = (offset.y / cellSize).toInt()
            val head = state.value.snake.first()

            _state.update {
                it.copy(
                    direction = when (state.value.direction) {
                        Direction.UP ,Direction.DOWN -> {
                            if (tapX < head.x) Direction.LEFT else Direction.RIGHT
                        }
                        Direction.LEFT ,Direction.RIGHT -> {
                            if (tapY < head.y) Direction.UP else Direction.DOWN
                        }
                    }
                )
            }
        }
    }

    private fun updateGame(currentGame: SnakeGameState): SnakeGameState {
        if (currentGame.isGameOver) {
            return currentGame // this will return the default state
        }

        val head = currentGame.snake.first()//this is targets the first index of list which will hold the head
        val xAxisGridSize = currentGame.xAxisGridSize
        val yAxisGridSize = currentGame.yAxisGridSize

        //Update movement of snake
        val newHead = when (currentGame.direction) {
            Direction.UP -> Coordinate(x = head.x, y = (head.y - 1))
            Direction.DOWN -> Coordinate(x = head.x, y = (head.y + 1))
            Direction.LEFT -> Coordinate(x = head.x - 1, y = (head.y))
            Direction.RIGHT -> Coordinate(x = head.x + 1, y = (head.y))
        }

        //Check if snake collides with itself or goes out of bounds
        if (
            currentGame.snake.contains(newHead) || !isWithinBounds(newHead, xAxisGridSize, yAxisGridSize)
        ) {
            return currentGame.copy(isGameOver = true)
        }

        //check if the snake eats the food
        var newSnake = mutableListOf(newHead) + currentGame.snake
        val newFood = if (newHead == currentGame.food) SnakeGameState.generateRandomFoodCoordinate() else currentGame.food

        //update snake length
        if (newHead != currentGame.food) {
            newSnake = newSnake.toMutableList()
            newSnake.removeAt(newSnake.size - 1)
        }

        return currentGame.copy(snake = newSnake, food = newFood)
    }

    private fun isWithinBounds(
        coordinate: Coordinate,
        xAxisGridSize: Int,
        yAxisGridSize: Int
    ): Boolean {
        return coordinate.x in 1 until xAxisGridSize - 1 && coordinate.y in 1 until yAxisGridSize - 1
    }
}