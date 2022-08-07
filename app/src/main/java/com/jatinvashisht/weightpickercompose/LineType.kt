package com.jatinvashisht.weightpickercompose

sealed class LineType{
    object Normal: LineType()
    object FiveStep: LineType()
    object TenStep: LineType()
}
