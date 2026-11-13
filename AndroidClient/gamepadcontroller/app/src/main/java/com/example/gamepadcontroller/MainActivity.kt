package com.example.gamepadcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.gamepadcontroller.ui.theme.GamepadcontrollerTheme


// --------------------------------------------------
// MAIN ACTIVITY
// --------------------------------------------------

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            GamepadcontrollerTheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    GamepadScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}


// --------------------------------------------------
// GAMEPAD SCREEN
// --------------------------------------------------

@Composable
fun GamepadScreen(modifier: Modifier = Modifier) {

    var controllerState by remember {
        mutableStateOf(ControllerState())
    }

    // D-PAD STATES

    var dpadUp by remember { mutableStateOf(false) }
    var dpadDown by remember { mutableStateOf(false) }
    var dpadLeft by remember { mutableStateOf(false) }
    var dpadRight by remember { mutableStateOf(false) }


    // D-PAD STATE CALCULATION

    LaunchedEffect(dpadUp, dpadDown, dpadLeft, dpadRight) {

        controllerState = controllerState.copy(

            dpadX = when {
                dpadLeft && !dpadRight -> -1
                dpadRight && !dpadLeft -> 1
                else -> 0
            },

            dpadY = when {
                dpadUp && !dpadDown -> -1
                dpadDown && !dpadUp -> 1
                else -> 0
            }
        )
    }


    // RESPONSIVE SCREEN

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {

        // Available screen dimensions

        val screenWidth = maxWidth
        val screenHeight = maxHeight


        // RESPONSIVE CONTROL SIZES

        val buttonSize = minOf(
            screenWidth * 0.075f,
            screenHeight * 0.15f
        ).coerceIn(36.dp, 58.dp)

        val dpadSize = buttonSize * 0.85f

        val stickSize = minOf(
            screenWidth * 0.17f,
            screenHeight * 0.36f
        ).coerceIn(90.dp, 145.dp)

        val triggerWidth = (screenWidth * 0.045f)
            .coerceIn(32.dp, 46.dp)

        val triggerHeight = minOf(
            screenHeight * 0.32f,
            125.dp
        ).coerceAtLeast(75.dp)


        // --------------------------------------------------
        // LEFT ANALOG STICK
        // --------------------------------------------------

        AnalogStick(
            label = "Left Stick",
            stickSize = stickSize,
            onPositionChange = { x, y ->

                controllerState = controllerState.copy(
                    leftStickX = x,
                    leftStickY = y
                )
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = screenWidth * 0.025f,
                    y = screenHeight * 0.58f
                )
        )


        // LEFT STICK CLICK

        GamepadButton(
            label = "LS",
            isPressed = controllerState.ls,
            buttonSize = buttonSize * 0.75f,
            onPressedChange = {

                controllerState = controllerState.copy(ls = it)
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = screenWidth * 0.17f,
                    y = screenHeight * 0.76f
                )
        )


        // --------------------------------------------------
        // LEFT SHOULDER BUTTON AND TRIGGER
        // --------------------------------------------------

        GamepadButton(
            label = "LB",
            isPressed = controllerState.lb,
            buttonSize = buttonSize,
            onPressedChange = {

                controllerState = controllerState.copy(lb = it)
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = screenWidth * 0.14f,
                    y = screenHeight * 0.035f
                )
        )


        TriggerSlider(
            label = "LT",
            value = controllerState.lt,
            sliderWidth = triggerWidth,
            sliderHeight = triggerHeight,
            onValueChange = {

                controllerState = controllerState.copy(lt = it)
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = screenWidth * 0.14f,
                    y = screenHeight * 0.23f
                )
        )


        // --------------------------------------------------
        // D-PAD
        // --------------------------------------------------

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = screenWidth * 0.28f,
                    y = screenHeight * 0.16f
                ),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.spacedBy(
                dpadSize * 0.08f
            )
        ) {

            DpadButton(
                label = "↑",
                isPressed = dpadUp,
                buttonSize = dpadSize,
                onPressedChange = { dpadUp = it }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dpadSize * 0.08f
                )
            ) {

                DpadButton(
                    label = "←",
                    isPressed = dpadLeft,
                    buttonSize = dpadSize,
                    onPressedChange = { dpadLeft = it }
                )

                DpadButton(
                    label = "→",
                    isPressed = dpadRight,
                    buttonSize = dpadSize,
                    onPressedChange = { dpadRight = it }
                )
            }

            DpadButton(
                label = "↓",
                isPressed = dpadDown,
                buttonSize = dpadSize,
                onPressedChange = { dpadDown = it }
            )
        }


        // --------------------------------------------------
        // CENTER SYSTEM BUTTONS
        // --------------------------------------------------

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(
                    y = -(screenHeight * 0.08f)
                ),

            horizontalArrangement = Arrangement.spacedBy(
                buttonSize * 0.15f
            ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            GamepadButton(
                label = "SELECT",
                isPressed = controllerState.select,
                buttonSize = buttonSize * 1.15f,
                onPressedChange = {

                    controllerState = controllerState.copy(select = it)
                }
            )

            GamepadButton(
                label = "GUIDE",
                isPressed = controllerState.guide,
                buttonSize = buttonSize * 1.15f,
                onPressedChange = {

                    controllerState = controllerState.copy(guide = it)
                }
            )

            GamepadButton(
                label = "START",
                isPressed = controllerState.start,
                buttonSize = buttonSize * 1.15f,
                onPressedChange = {

                    controllerState = controllerState.copy(start = it)
                }
            )
        }


        // --------------------------------------------------
        // RIGHT ANALOG STICK
        // --------------------------------------------------

        AnalogStick(
            label = "Right Stick",
            stickSize = stickSize,
            onPositionChange = { x, y ->

                controllerState = controllerState.copy(
                    rightStickX = x,
                    rightStickY = y
                )
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(
                    x = -(screenWidth * 0.025f),
                    y = screenHeight * 0.58f
                )
        )


        // RIGHT STICK CLICK

        GamepadButton(
            label = "RS",
            isPressed = controllerState.rs,
            buttonSize = buttonSize * 0.75f,
            onPressedChange = {

                controllerState = controllerState.copy(rs = it)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(
                    x = -(screenWidth * 0.17f),
                    y = screenHeight * 0.76f
                )
        )


        // --------------------------------------------------
        // RIGHT SHOULDER BUTTON AND TRIGGER
        // --------------------------------------------------

        GamepadButton(
            label = "RB",
            isPressed = controllerState.rb,
            buttonSize = buttonSize,
            onPressedChange = {

                controllerState = controllerState.copy(rb = it)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(
                    x = -(screenWidth * 0.14f),
                    y = screenHeight * 0.035f
                )
        )


        TriggerSlider(
            label = "RT",
            value = controllerState.rt,
            sliderWidth = triggerWidth,
            sliderHeight = triggerHeight,
            onValueChange = {

                controllerState = controllerState.copy(rt = it)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(
                    x = -(screenWidth * 0.14f),
                    y = screenHeight * 0.23f
                )
        )


        // --------------------------------------------------
        // PLAYSTATION FACE BUTTONS
        // --------------------------------------------------

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(
                    x = -(screenWidth * 0.28f),
                    y = screenHeight * 0.16f
                ),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.spacedBy(
                buttonSize * 0.12f
            )
        ) {

            // TRIANGLE

            GamepadButton(
                label = "△",
                isPressed = controllerState.y,
                buttonSize = buttonSize,
                onPressedChange = {

                    controllerState = controllerState.copy(y = it)
                }
            )


            // SQUARE AND CIRCLE

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    buttonSize * 0.12f
                )
            ) {

                GamepadButton(
                    label = "□",
                    isPressed = controllerState.x,
                    buttonSize = buttonSize,
                    onPressedChange = {

                        controllerState = controllerState.copy(x = it)
                    }
                )

                GamepadButton(
                    label = "○",
                    isPressed = controllerState.b,
                    buttonSize = buttonSize,
                    onPressedChange = {

                        controllerState = controllerState.copy(b = it)
                    }
                )
            }


            // CROSS

            GamepadButton(
                label = "✕",
                isPressed = controllerState.a,
                buttonSize = buttonSize,
                onPressedChange = {

                    controllerState = controllerState.copy(a = it)
                }
            )
        }


        // --------------------------------------------------
        // FLOATING STATUS DISPLAY
        // --------------------------------------------------

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp)
                .widthIn(max = screenWidth * 0.48f)
                .background(
                    color = Color.Black.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(
                    horizontal = 8.dp,
                    vertical = 5.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = """
                    A:${controllerState.a} B:${controllerState.b} X:${controllerState.x} Y:${controllerState.y}
                    LB:${controllerState.lb} RB:${controllerState.rb} | LS:${controllerState.ls} RS:${controllerState.rs}
                    D-pad: ${controllerState.dpadX}, ${controllerState.dpadY}
                    Left: ${controllerState.leftStickX}, ${controllerState.leftStickY}
                    Right: ${controllerState.rightStickX}, ${controllerState.rightStickY}
                    LT:${controllerState.lt} RT:${controllerState.rt}
                """.trimIndent(),

                color = Color.White,

                fontSize = 9.sp,

                lineHeight = 11.sp
            )
        }
    }
}


// --------------------------------------------------
// REUSABLE GAMEPAD BUTTON
// --------------------------------------------------

@Composable
fun GamepadButton(
    label: String,
    isPressed: Boolean,
    onPressedChange: (Boolean) -> Unit,
    buttonSize: Dp = 75.dp,
    modifier: Modifier = Modifier
) {

    val currentOnPressedChange by rememberUpdatedState(onPressedChange)

    Box(
        modifier = modifier
            .size(buttonSize)
            .background(
                color = if (isPressed) Color.Red else Color.DarkGray,
                shape = RoundedCornerShape(50.dp)
            )
            .pointerInput(Unit) {

                detectTapGestures(
                    onPress = {

                        currentOnPressedChange(true)

                        try {
                            tryAwaitRelease()
                        } finally {
                            currentOnPressedChange(false)
                        }
                    }
                )
            },

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = label,
            color = Color.White,
            fontSize = if (label.length > 2) 9.sp else 14.sp
        )
    }
}


// --------------------------------------------------
// REUSABLE D-PAD BUTTON
// --------------------------------------------------

@Composable
fun DpadButton(
    label: String,
    isPressed: Boolean,
    onPressedChange: (Boolean) -> Unit,
    buttonSize: Dp = 65.dp
) {

    val currentOnPressedChange by rememberUpdatedState(onPressedChange)

    Box(
        modifier = Modifier
            .size(buttonSize)
            .background(
                color = if (isPressed) Color.Red else Color.DarkGray,
                shape = RoundedCornerShape(12.dp)
            )
            .pointerInput(Unit) {

                detectTapGestures(
                    onPress = {

                        currentOnPressedChange(true)

                        try {
                            tryAwaitRelease()
                        } finally {
                            currentOnPressedChange(false)
                        }
                    }
                )
            },

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = label,
            color = Color.White,
            fontSize = 18.sp
        )
    }
}


// --------------------------------------------------
// ANALOG STICK
// --------------------------------------------------

@Composable
fun AnalogStick(
    label: String,
    onPositionChange: (Float, Float) -> Unit,
    stickSize: Dp = 160.dp,
    modifier: Modifier = Modifier
) {

    var knobPosition by remember {
        mutableStateOf(Offset.Zero)
    }

    val currentOnPositionChange by rememberUpdatedState(onPositionChange)


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp
        )


        Canvas(
            modifier = Modifier
                .size(stickSize)
                .pointerInput(Unit) {

                    fun updatePosition(touch: Offset) {

                        val center = Offset(
                            size.width / 2f,
                            size.height / 2f
                        )

                        val baseRadius =
                            minOf(size.width, size.height).toFloat() * 0.46f

                        val knobRadius = baseRadius * 0.32f

                        val maxDistance = baseRadius - knobRadius


                        val dx = touch.x - center.x
                        val dy = touch.y - center.y

                        val distance = kotlin.math.hypot(dx, dy)


                        val scale = if (distance > maxDistance) {
                            maxDistance / distance
                        } else {
                            1f
                        }


                        val offsetX = dx * scale
                        val offsetY = dy * scale

                        knobPosition = Offset(offsetX, offsetY)


                        val normalizedX =
                            (offsetX / maxDistance).coerceIn(-1f, 1f)

                        val normalizedY =
                            (offsetY / maxDistance).coerceIn(-1f, 1f)


                        currentOnPositionChange(
                            normalizedX,
                            normalizedY
                        )
                    }


                    detectDragGestures(

                        onDragStart = {
                            updatePosition(it)
                        },

                        onDrag = { change, _ ->

                            change.consume()

                            updatePosition(change.position)
                        },

                        onDragEnd = {

                            knobPosition = Offset.Zero

                            currentOnPositionChange(0f, 0f)
                        },

                        onDragCancel = {

                            knobPosition = Offset.Zero

                            currentOnPositionChange(0f, 0f)
                        }
                    )
                }
        ) {

            val center = Offset(
                size.width / 2f,
                size.height / 2f
            )

            val baseRadius = size.minDimension * 0.46f
            val knobRadius = baseRadius * 0.32f


            // OUTER BASE

            drawCircle(
                color = Color.DarkGray,
                radius = baseRadius,
                center = center
            )


            // MOVABLE KNOB

            drawCircle(
                color = Color.LightGray,
                radius = knobRadius,
                center = center + knobPosition
            )
        }
    }
}


// --------------------------------------------------
// ANALOG TRIGGER
// --------------------------------------------------

@Composable
fun TriggerSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    sliderWidth: Dp = 65.dp,
    sliderHeight: Dp = 180.dp,
    modifier: Modifier = Modifier
) {

    val currentOnValueChange by rememberUpdatedState(onValueChange)


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "$label: ${(value * 100).toInt()}%",
            color = Color.White,
            fontSize = 10.sp
        )


        Box(
            modifier = Modifier
                .width(sliderWidth)
                .height(sliderHeight)
                .background(
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(12.dp)
                )
                .pointerInput(Unit) {

                    detectDragGestures(

                        onDragStart = { touch ->

                            val newValue =
                                (1f - touch.y / size.height)
                                    .coerceIn(0f, 1f)

                            currentOnValueChange(newValue)
                        },

                        onDrag = { change, _ ->

                            change.consume()

                            val newValue =
                                (1f - change.position.y / size.height)
                                    .coerceIn(0f, 1f)

                            currentOnValueChange(newValue)
                        },

                        onDragEnd = {
                            currentOnValueChange(0f)
                        },

                        onDragCancel = {
                            currentOnValueChange(0f)
                        }
                    )
                },

            contentAlignment = Alignment.BottomCenter
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(value.coerceIn(0f, 1f))
                    .background(
                        color = Color.Green,
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }
    }
}