package com.jatinvashisht.weightpickercompose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset

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

    Canvas(modifier = modifier){

    }
}