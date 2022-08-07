package com.jatinvashisht.weightpickercompose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// will act as wrapper class for styles of our scale
data class ScaleStyle(
    val scaleWidth: Dp = 100.dp,
    val radius: Dp = 555.dp,
    val normalLineColor: Color = Color.LightGray,
    val fiveStepLineColor: Color = Color.Green,
    val tenStepLineColor: Color = Color.Black,
    val normalLineLength: Dp = 15.dp,
    val fieStepLineLength: Dp = 25.dp,
    val tenStepLineLength: Dp = 35.dp,
    val scaleIndicatorColor: Color = Color.Green,
    val scaleIndicatorLength: Dp = 60.dp,
    val textStyle: TextUnit = 18.sp,
)