package com.example.gamepadcontroller

data class ControllerState(
    // Face buttons
    val a: Boolean = false,
    val b: Boolean = false,
    val x: Boolean = false,
    val y: Boolean = false,

    // Shoulder buttons
    val lb: Boolean = false,
    val rb: Boolean = false,

    // Stick clicks
    val ls: Boolean = false,
    val rs: Boolean = false,

    // System buttons
    val start: Boolean = false,
    val select: Boolean = false,
    val guide: Boolean = false,

    // D-pad
    val dpadX: Int = 0,
    val dpadY: Int = 0,

    // Analog sticks
    val leftStickX: Float = 0f,
    val leftStickY: Float = 0f,
    val rightStickX: Float = 0f,
    val rightStickY: Float = 0f,

    // Analog triggers
    val lt: Float = 0f,
    val rt: Float = 0f
)