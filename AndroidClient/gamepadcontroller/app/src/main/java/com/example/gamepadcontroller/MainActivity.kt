package com.example.gamepadcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.gamepadcontroller.ui.theme.GamepadcontrollerTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height

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
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun GamepadScreen(modifier: Modifier = Modifier) {

    var controllerState by remember {
        mutableStateOf(ControllerState())
    }
    // Dpad state remember
    var dpadUp by remember { mutableStateOf(false) }
    var dpadDown by remember { mutableStateOf(false) }
    var dpadLeft by remember { mutableStateOf(false) }
    var dpadRight by remember { mutableStateOf(false) }
    // Logic of dpad state?
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

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        // FIXED STATUS DISPLAY
        Text(
            text = """
            Face: A=${controllerState.a} B=${controllerState.b} X=${controllerState.x} Y=${controllerState.y}
            Shoulder: LB=${controllerState.lb} RB=${controllerState.rb}
            Stick Clicks: LS=${controllerState.ls} RS=${controllerState.rs}
            System: Start=${controllerState.start} Select=${controllerState.select} Guide=${controllerState.guide}
            D-pad: X=${controllerState.dpadX}, Y=${controllerState.dpadY}
            Left Stick: X=${controllerState.leftStickX}, Y=${controllerState.leftStickY}
            Right Stick: X=${controllerState.rightStickX}, Y=${controllerState.rightStickY}
            Triggers: LT=${controllerState.lt}, RT=${controllerState.rt}
        """.trimIndent(),
            modifier = Modifier.padding(8.dp),
            fontSize = 11.sp,
            lineHeight = 15.sp
        )

        // SCROLLABLE CONTROLLER AREA
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Face Buttons
            Spacer(modifier = Modifier.size(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GamepadButton(
                    label = "Y",
                    isPressed = controllerState.y,
                    onPressedChange = {
                        controllerState = controllerState.copy(y = it)
                    }
                )

                GamepadButton(
                    label = "X",
                    isPressed = controllerState.x,
                    onPressedChange = {
                        controllerState = controllerState.copy(x = it)
                    }
                )

                GamepadButton(
                    label = "B",
                    isPressed = controllerState.b,
                    onPressedChange = {
                        controllerState = controllerState.copy(b = it)
                    }
                )

                GamepadButton(
                    label = "A",
                    isPressed = controllerState.a,
                    onPressedChange = {
                        controllerState = controllerState.copy(a = it)
                    }
                )
            }

            // Shoulder Buttons
            Spacer(modifier = Modifier.size(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                GamepadButton(
                    label = "LB",
                    isPressed = controllerState.lb,
                    onPressedChange = {
                        controllerState = controllerState.copy(lb = it)
                    }
                )

                GamepadButton(
                    label = "RB",
                    isPressed = controllerState.rb,
                    onPressedChange = {
                        controllerState = controllerState.copy(rb = it)
                    }
                )
            }

            // Stick Click Buttons
            Spacer(modifier = Modifier.size(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                GamepadButton(
                    label = "LS",
                    isPressed = controllerState.ls,
                    onPressedChange = {
                        controllerState = controllerState.copy(ls = it)
                    }
                )

                GamepadButton(
                    label = "RS",
                    isPressed = controllerState.rs,
                    onPressedChange = {
                        controllerState = controllerState.copy(rs = it)
                    }
                )
            }

            // Start, Select and Guide
            Spacer(modifier = Modifier.size(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                GamepadButton(
                    label = "Select",
                    isPressed = controllerState.select,
                    onPressedChange = {
                        controllerState = controllerState.copy(select = it)
                    }
                )

                GamepadButton(
                    label = "Start",
                    isPressed = controllerState.start,
                    onPressedChange = {
                        controllerState = controllerState.copy(start = it)
                    }
                )

                GamepadButton(
                    label = "Guide",
                    isPressed = controllerState.guide,
                    onPressedChange = {
                        controllerState = controllerState.copy(guide = it)
                    }
                )
            }

            // D-pad
            Spacer(modifier = Modifier.size(30.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DpadButton(
                    label = "↑",
                    isPressed = dpadUp,
                    onPressedChange = {
                        dpadUp = it
                    }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DpadButton(
                        label = "←",
                        isPressed = dpadLeft,
                        onPressedChange = {
                            dpadLeft = it
                        }
                    )

                    DpadButton(
                        label = "→",
                        isPressed = dpadRight,
                        onPressedChange = {
                            dpadRight = it
                        }
                    )
                }

                DpadButton(
                    label = "↓",
                    isPressed = dpadDown,
                    onPressedChange = {
                        dpadDown = it
                    }
                )
            }

            // Left Analog Stick
            Spacer(modifier = Modifier.size(30.dp))

            AnalogStick(
                label = "Left Stick",
                onPositionChange = { x, y ->
                    controllerState = controllerState.copy(
                        leftStickX = x,
                        leftStickY = y
                    )
                }
            )

            // Right Analog Stick
            Spacer(modifier = Modifier.size(30.dp))

            AnalogStick(
                label = "Right Stick",
                onPositionChange = { x, y ->
                    controllerState = controllerState.copy(
                        rightStickX = x,
                        rightStickY = y
                    )
                }
            )

            // Triggers Left and right
            Row(
                horizontalArrangement = Arrangement.spacedBy(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TriggerSlider(
                    label = "LT",
                    value = controllerState.lt,
                    onValueChange = {
                        controllerState = controllerState.copy(lt = it)
                    }
                )

                TriggerSlider(
                    label = "RT",
                    value = controllerState.rt,
                    onValueChange = {
                        controllerState = controllerState.copy(rt = it)
                    }
                )
            }

            // Bottom spacing
            Spacer(modifier = Modifier.size(30.dp))
        }
    }
}
@Composable
fun GamepadButton(
    label: String,
    isPressed: Boolean,
    onPressedChange: (Boolean) -> Unit
) {
    val currentOnPressedChange by rememberUpdatedState(onPressedChange)

    Box(
        modifier = Modifier
            .size(75.dp)
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
            color = Color.White
        )
    }
}

// Dpad buttons composable
@Composable
fun DpadButton(
    label: String,
    isPressed: Boolean,
    onPressedChange: (Boolean) -> Unit
) {
    val currentOnPressedChange by rememberUpdatedState(onPressedChange)

    Box(
        modifier = Modifier
            .size(65.dp)
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
            color = Color.White
        )
    }
}

@Composable
fun AnalogStick(
    label: String,
    onPositionChange: (Float, Float) -> Unit
) {
    var knobPosition by remember {
        mutableStateOf(Offset.Zero)
    }

    val currentOnPositionChange by rememberUpdatedState(onPositionChange)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label)

        Canvas(
            modifier = Modifier
                .size(160.dp)
                .pointerInput(Unit) {
                    fun updatePosition(touch: Offset) {
                        val center = Offset(
                            size.width / 2f,
                            size.height / 2f
                        )

                        val baseRadius = minOf(size.width, size.height).toFloat() * 0.46f
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

                        val normalizedX = (offsetX / maxDistance).coerceIn(-1f, 1f)
                        val normalizedY = (offsetY / maxDistance).coerceIn(-1f, 1f)

                        currentOnPositionChange(normalizedX, normalizedY)
                    }

                    detectDragGestures(
                        onDragStart = { updatePosition(it) },
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

            // Outer joystick base
            drawCircle(
                color = Color.DarkGray,
                radius = baseRadius,
                center = center
            )

            // Movable inner knob
            drawCircle(
                color = Color.LightGray,
                radius = knobRadius,
                center = center + knobPosition
            )
        }
    }
}

// Trigger logic
@Composable
fun TriggerSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "$label: ${(value * 100).toInt()}%")

        Box(
            modifier = Modifier
                .width(65.dp)
                .height(180.dp)
                .background(
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(12.dp)
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { touch ->
                            val newValue =
                                (1f - touch.y / size.height).coerceIn(0f, 1f)
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