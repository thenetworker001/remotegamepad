from evdev import UInput, ecodes, AbsInfo

# -----------------------------
# BUTTON DEFINITIONS
# -----------------------------

BUTTONS = {
    "a": ecodes.BTN_SOUTH,
    "b": ecodes.BTN_EAST,
    "x": ecodes.BTN_WEST,
    "y": ecodes.BTN_NORTH,

    "lb": ecodes.BTN_TL,
    "rb": ecodes.BTN_TR,

    "ls": ecodes.BTN_THUMBL,
    "rs": ecodes.BTN_THUMBR,

    "start": ecodes.BTN_START,
    "select": ecodes.BTN_SELECT,
    "guide": ecodes.BTN_MODE,
}

# -----------------------------
# AXIS DEFINITIONS
# -----------------------------

STICK_AXES = {
    "lx": ecodes.ABS_X,
    "ly": ecodes.ABS_Y,
    "rx": ecodes.ABS_RX,
    "ry": ecodes.ABS_RY,
}

TRIGGER_AXES = {
    "lt": ecodes.ABS_Z,
    "rt": ecodes.ABS_RZ,
}

# -----------------------------
# DEVICE CAPABILITIES
# -----------------------------

axes = [
    (code, AbsInfo(0, -32768, 32767, 0, 0, 0))
    for code in STICK_AXES.values()
]

axes += [
    (code, AbsInfo(0, 0, 255, 0, 0, 0))
    for code in TRIGGER_AXES.values()
]

axes += [
    (ecodes.ABS_HAT0X, AbsInfo(0, -1, 1, 0, 0, 0)),
    (ecodes.ABS_HAT0Y, AbsInfo(0, -1, 1, 0, 0, 0)),
]

capabilities = {
    ecodes.EV_KEY: list(BUTTONS.values()),
    ecodes.EV_ABS: axes,
}

# -----------------------------
# CREATE VIRTUAL GAMEPAD
# -----------------------------

gamepad = UInput(
    capabilities,
    name="Stark Virtual Gamepad",
    vendor=0x1234,
    product=0x5678,
    version=1
)

print("\nStark Virtual Gamepad is ready!")
print("Type 'help' to see available commands.\n")


# -----------------------------
# CURRENT D-PAD STATE
# -----------------------------

# X: -1 = left, 0 = center, 1 = right
# Y: -1 = up,   0 = center, 1 = down

dpad_x = 0
dpad_y = 0


# -----------------------------
# INPUT FUNCTIONS
# -----------------------------

def press_button(name, pressed):
    code = BUTTONS[name]

    gamepad.write(
        ecodes.EV_KEY,
        code,
        int(pressed)
    )

    gamepad.syn()


def set_stick(axis, value):
    if not -1.0 <= value <= 1.0:
        raise ValueError("Stick value must be between -1 and 1")

    code = STICK_AXES[axis]

    if value < 0:
        raw_value = round(value * 32768)
    else:
        raw_value = round(value * 32767)

    gamepad.write(ecodes.EV_ABS, code, raw_value)
    gamepad.syn()


def set_trigger(name, value):
    if not 0.0 <= value <= 1.0:
        raise ValueError("Trigger value must be between 0 and 1")

    code = TRIGGER_AXES[name]
    raw_value = round(value * 255)

    gamepad.write(ecodes.EV_ABS, code, raw_value)
    gamepad.syn()


def set_dpad(direction):
    global dpad_x, dpad_y

    if direction == "up":
        dpad_y = -1

    elif direction == "down":
        dpad_y = 1

    elif direction == "left":
        dpad_x = -1

    elif direction == "right":
        dpad_x = 1

    elif direction == "center":
        dpad_x = 0
        dpad_y = 0

    else:
        raise ValueError("Invalid D-pad direction")

    # Send BOTH axes using their current states.
    # This preserves diagonal directions.

    gamepad.write(
        ecodes.EV_ABS,
        ecodes.ABS_HAT0X,
        dpad_x
    )

    gamepad.write(
        ecodes.EV_ABS,
        ecodes.ABS_HAT0Y,
        dpad_y
    )

    gamepad.syn()


# -----------------------------
# COMMAND INTERFACE
# -----------------------------

def show_help():
    print("""
Available commands:

Buttons:
    press a
    release a

    Buttons: a b x y lb rb ls rs start select guide

Analog sticks:
    stick lx 0.5
    stick ly -1
    stick rx 0
    stick ry 1

Triggers:
    trigger lt 0.5
    trigger rt 1

D-pad:
    dpad up
    dpad down
    dpad left
    dpad right
    dpad center

Other:
    help
    quit
""")


# -----------------------------
# MAIN PROGRAM LOOP
# -----------------------------

try:
    while True:
        command = input("gamepad> ").strip().lower().split()

        if not command:
            continue

        try:
            if command[0] == "quit":
                break

            elif command[0] == "help":
                show_help()

            elif command[0] in ("press", "release"):
                if len(command) != 2 or command[1] not in BUTTONS:
                    print("Invalid button.")
                    continue

                press_button(
                    command[1],
                    command[0] == "press"
                )

            elif command[0] == "stick":
                if len(command) != 3 or command[1] not in STICK_AXES:
                    print("Usage: stick <lx|ly|rx|ry> <value>")
                    continue

                value = float(command[2])
                set_stick(command[1], value)

            elif command[0] == "trigger":
                if len(command) != 3 or command[1] not in TRIGGER_AXES:
                    print("Usage: trigger <lt|rt> <value>")
                    continue

                value = float(command[2])
                set_trigger(command[1], value)

            elif command[0] == "dpad":
                if len(command) != 2:
                    print("Usage: dpad <direction>")
                    continue

                set_dpad(command[1])

            else:
                print("Unknown command. Type 'help'.")

        except ValueError as error:
            print(f"Invalid input: {error}")

except (KeyboardInterrupt, EOFError):
    print("\nExiting...")

finally:
    gamepad.close()
    print("Virtual gamepad closed.")
