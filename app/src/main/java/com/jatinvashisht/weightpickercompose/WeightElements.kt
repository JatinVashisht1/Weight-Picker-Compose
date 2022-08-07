package com.jatinvashisht.weightpickercompose

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.graphics.withRotation
import kotlin.math.*

@Composable
fun Scale(
    modifier: Modifier = Modifier,
    style: ScaleStyle = ScaleStyle(),
    // it will look messy to pass all styles as parameters
    // we are using a wrapper class to define styling of our scale
    // it is similar to the approach used by TextField composable, which also takes object
    // of TextStyle as an input parameter
    minWeight: Int = 20,
    maxWeight: Int = 250,
    initialWeight: Int = 80,
    onWeightChange: (Int) -> Unit,
) {
    val radius = style.radius
    val scaleWidth = style.scaleWidth

    // center of canvas co-ordinate system
    var center by remember{
        mutableStateOf(Offset.Zero)
    }

    // center of circle
    var circleCenter by remember{
        mutableStateOf(Offset.Zero)
    }

    var angle by remember{
        mutableStateOf(0f)
    }

    // when we want to drag our scale, we want difference between angle at which started the drag and angle at which we stopped the drag
    var dragStartedAngle by remember {
        mutableStateOf(0f)
    }

    var oldAngle by remember{
        mutableStateOf(angle)
    }

    Canvas(
        modifier = modifier
            .pointerInput(true) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // to calculate angle between two points
                        // we can use atan function of geometry
                        dragStartedAngle = -atan2(
                            circleCenter.x - offset.x,
                            circleCenter.y - offset.y
                        ) * (180f / PI.toFloat())
                    },
                    onDragEnd = {
                        oldAngle = angle
                    }

                ) { change, _ ->
                    val touchAngle = -atan2(
                        circleCenter.x - change.position.x,
                        circleCenter.y - change.position.y
                    ) * (180f / PI.toFloat())
                    val newAngle = oldAngle + (touchAngle - dragStartedAngle)
                    angle = newAngle.coerceIn(
                        // we are subtracting minimum weight in case of maximum value and vice versa is because when we increase the weight the angle actually decreases
                        minimumValue = initialWeight - maxWeight.toFloat(),
                        maximumValue = initialWeight - minWeight.toFloat()
                    )
                    onWeightChange((initialWeight - angle).roundToInt())
                }
            }
    ){
        // making state variable state equals to the center of our draw scope
        center = this.center
        // pointing x co-ordinate of circle to the center of our draw scope
        // pointing y co-ordinate of circle to bottom center of circle
        circleCenter = Offset(
            center.x,
            scaleWidth.toPx() / 2f + radius.toPx()
        )

        // defining outer/bigger circle radius
        val outerRadius = radius.toPx() + scaleWidth.toPx() / 2f
        // defining inner/smaller circle radius
        // similar logic to outer radius just subtracting scale width instead of adding it
        val innerRadius = radius.toPx() - scaleWidth.toPx() / 2f

        drawContext.canvas.nativeCanvas.apply {
            drawCircle(
                circleCenter.x,
                circleCenter.y,
                radius.toPx(),
                Paint().apply {
                    strokeWidth = scaleWidth.toPx()
                    color = Color.WHITE
                    // telling canvas only draw stroke and not fill color
                    setStyle(Paint.Style.STROKE)
                    setShadowLayer(
                        60f,
                        // dx and dy means shifting of shadow from original position in x and y directions
                        0f,
                        0f,
                        Color.argb(50, 0,0,0),
                    )
                }
            )
        }

        // Draw lines
        for(weight in minWeight .. maxWeight){
            // calculating angle of line in radians for each weight
            // angle with which the lines are rotated, they are looking at our center of circle
            val angleInRad = ((weight - initialWeight + angle - 90) * (PI / 180f)).toFloat()
            val lineType = when{
                weight % 10 == 0 -> LineType.TenStep
                weight % 5 == 0 -> LineType.FiveStep
                else -> LineType.Normal
            }

            val lineLength = when(lineType){
                LineType.Normal -> style.normalLineLength.toPx()
                LineType.FiveStep -> style.fiveStepLineLength.toPx()
                LineType.TenStep -> style.tenStepLineLength.toPx()
            }

            val lineColor = when(lineType){
                LineType.Normal -> style.normalLineColor
                LineType.FiveStep -> style.fiveStepLineColor
                LineType.TenStep -> style.tenStepLineColor
            }

            val lineStart = Offset(
                // calculating the x co-ordinate of our line
                // we are adding circleCenter.x because we have to use the center of our circle
                // not the center of our screen
                x  = (outerRadius - lineLength ) * cos(angleInRad) + circleCenter.x,
                y = (outerRadius - lineLength) * sin(angleInRad) + circleCenter.y
            )

            val lineEnd = Offset(
                // not subtracting line length because we want line to end at our co-ordinate radius
                x  = (outerRadius) * cos(angleInRad) + circleCenter.x,
                y = (outerRadius) * sin(angleInRad) + circleCenter.y
            )

            drawContext.canvas.nativeCanvas.apply{
                if(lineType == LineType.TenStep){
                    // we are calculating the x position of our text int the same way we calculated for our lines
                    // but we are subtracting addition 5 dp and textSize because we want to draw text below the ten step line

                    val textRadius = (outerRadius - lineLength - 5.dp.toPx() - style.textSize.toPx())
                    val x = textRadius * cos(angleInRad) + circleCenter.x
                    val y = textRadius * sin(angleInRad) + circleCenter.y
                    // any thing we draw in this lambda block will be rotated by the degrees we specify
                    withRotation(
                        degrees = angleInRad * (180f/PI.toFloat()) + 90f,
                        pivotX = x,
                        pivotY = y,
                    ) {
                        drawText(
                            abs(weight).toString(),
                            x,
                            y,
                            Paint().apply {
                                textSize = style.textSize.toPx()
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    }
                }
            }

            drawLine(
                color = lineColor,
                start = lineStart,
                end = lineEnd,
                strokeWidth = 1.dp.toPx(),
            )

            val middleTop = Offset(
                x = circleCenter.x,
                y = circleCenter.y - innerRadius - style.scaleIndicatorLength.toPx()
            )
            
            val bottomLeft = Offset(
                x = circleCenter.x - 4f,
                y = circleCenter.y - innerRadius
            )
            
            val bottomRight = Offset(
                x = circleCenter.x + 4f,
                y = circleCenter.y - innerRadius
            )

            val indicator = Path().apply{
                moveTo(middleTop.x, middleTop.y)
                lineTo(bottomLeft.x, bottomLeft.y)
                lineTo(bottomRight.x, bottomRight.y)
                lineTo(middleTop.x, middleTop.y)
            }
            drawPath(indicator, style.scaleIndicatorColor)
        }
    }
}