package com.example.snake

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.snake.ui.theme.Blue
import com.example.snake.ui.theme.Red
import com.example.snake.ui.theme.SnakeTheme

@Composable
fun SnakeGameScreen(
    modifier: Modifier = Modifier,
    state: SnakeGameState
) {
    
    val foodImageBitmap = ImageBitmap.imageResource(id = R.drawable.img_apple)
    
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Card(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = modifier.padding(16.dp),
                text = "Score: ${state.snake.size - 1}",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 2 / 3f)//2height and 3width
        ) {
            val cellSize = size.width / 20
            drawGameBoard(
                cellSize = cellSize,
                cellColor = Red,
                borderCellColor = Blue,
                gridWidth = state.xAxisGridSize,
                gridHeight = state.yAxisGridSize
            )
            drawFood(
                foodImage = foodImageBitmap ,//the food image from res
                cellSize = cellSize.toInt(),
                coordinate = state.food
            )
        }
        Row(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Button(
                modifier = modifier.weight(1f),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Replay")
            }
            Spacer(modifier = modifier.width(10.dp))
            Button(
                modifier = modifier.weight(1f),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Start")
            }
        }
    }

}

fun DrawScope.drawGameBoard(
    cellSize: Float,
    cellColor: Color,
    borderCellColor: Color,
    gridWidth: Int,
    gridHeight: Int
) {
    for (i in 0 until gridWidth) {
        for (j in 0 until gridHeight) {
            val isBorderCell = i == 0 || j == 0 || i == gridWidth - 1 || j == gridHeight - 1
            drawRect(
                color = if (isBorderCell) borderCellColor
                    else if ((i + j) % 2 == 0) cellColor else cellColor.copy(alpha = 0.5f),
                topLeft = Offset(x = i * cellSize, y = j * cellSize),
                size = Size(cellSize, cellSize)
            )
        }
    }
}

private fun DrawScope.drawFood(
    foodImage: ImageBitmap,
    cellSize: Int,
    coordinate: Coordinate
) {
    drawImage(
        image = foodImage,
        dstOffset = IntOffset( //destination Offset
            x = (coordinate.x * cellSize),
            y = (coordinate.x * cellSize)
        ),
        dstSize = IntSize(cellSize, cellSize) //destination Size
    )
}

@Preview(showBackground = true)
@Composable
private fun SnakePreview() {
    SnakeTheme {
        SnakeGameScreen(
            state = SnakeGameState()
        )
    }
}