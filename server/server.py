import socket
import json
import time

from evdev import UInput, ecodes, AbsInfo


# -----------------------------
# Configuration
# -----------------------------

HOST = "0.0.0.0"
PORT = 5005

PACKET_TIMEOUT = 0.5


# -----------------------------
# Button mappings
# -----------------------------

BUTTON_MAP = {
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
# Virtual gamepad creation
# -----------------------------

def create_gamepad():

    capabilities = {
        ecodes.EV_KEY: list(BUTTON_MAP.values()),

        ecodes.EV_ABS: [
            (ecodes.ABS_X, AbsInfo(0, -32768, 32767, 0, 0, 0)),
            (ecodes.ABS_Y, AbsInfo(0, -32768, 32767, 0, 0, 0)),

            (ecodes.ABS_RX, AbsInfo(0, -32768, 32767, 0, 0, 0)),
            (ecodes.ABS_RY, AbsInfo(0, -32768, 32767, 0, 0, 0)),

            (ecodes.ABS_Z, AbsInfo(0, 0, 255, 0, 0, 0)),
            (ecodes.ABS_RZ, AbsInfo(0, 0, 255, 0, 0, 0)),

            (ecodes.ABS_HAT0X, AbsInfo(0, -1, 1, 0, 0, 0)),
            (ecodes.ABS_HAT0Y, AbsInfo(0, -1, 1, 0, 0, 0)),
        ]
    }

    gamepad = UInput(
        capabilities,
        name="UDP Virtual Gamepad",
        vendor=0x1234,
        product=0x5678
    )

    print("Virtual gamepad created.")

    return gamepad


# -----------------------------
# Convert normalized values
# -----------------------------

def stick_value(value):

    if not -1.0 <= value <= 1.0:
        raise ValueError("Stick value outside -1 to 1")

    return round(value * 32767)


def trigger_value(value):

    if not 0.0 <= value <= 1.0:
        raise ValueError("Trigger value outside 0 to 1")

    return round(value * 255)


# -----------------------------
# Validate controller state
# -----------------------------

def parse_state(data):

    state = json.loads(data.decode("utf-8"))

    buttons = state["buttons"]
    sticks = state["sticks"]
    triggers = state["triggers"]
    dpad = state["dpad"]

    # Validate buttons
    for name in BUTTON_MAP:
        if not isinstance(buttons[name], bool):
            raise ValueError(f"Invalid button: {name}")

    # Validate sticks
    for name in ["lx", "ly", "rx", "ry"]:
        value = sticks[name]

        if isinstance(value, bool) or not isinstance(value, (int, float)):
            raise ValueError(f"Invalid stick: {name}")

        stick_value(value)

    # Validate triggers
    for name in ["lt", "rt"]:
        value = triggers[name]

        if isinstance(value, bool) or not isinstance(value, (int, float)):
            raise ValueError(f"Invalid trigger: {name}")

        trigger_value(value)

    # Validate D-pad
    if dpad["x"] not in (-1, 0, 1):
        raise ValueError("Invalid D-pad X")

    if dpad["y"] not in (-1, 0, 1):
        raise ValueError("Invalid D-pad Y")

    return state


# -----------------------------
# Apply state to virtual pad
# -----------------------------

def apply_state(gamepad, state):

    buttons = state["buttons"]
    sticks = state["sticks"]
    triggers = state["triggers"]
    dpad = state["dpad"]

    # Buttons
    for name, code in BUTTON_MAP.items():

        gamepad.write(
            ecodes.EV_KEY,
            code,
            int(buttons[name])
        )

    # Left stick
    gamepad.write(
        ecodes.EV_ABS,
        ecodes.ABS_X,
        stick_value(sticks["lx"])
    )

    gamepad.write(
        ecodes.EV_ABS,
        ecodes.ABS_Y,
        stick_value(sticks["ly"])
    )

    # Right stick
    gamepad.write(
        ecodes.EV_ABS,
        ecodes.ABS_RX,
        stick_value(sticks["rx"])
    )

    gamepad.write(
        ecodes.EV_ABS,
        ecodes.ABS_RY,
        stick_value(sticks["ry"])
    )

    # Triggers
    gamepad.write(
        ecodes.EV_ABS,
        ecodes.ABS_Z,
        trigger_value(triggers["lt"])
    )

    gamepad.write(
        ecodes.EV_ABS,
        ecodes.ABS_RZ,
        trigger_value(triggers["rt"])
    )

    # D-pad
    gamepad.write(
        ecodes.EV_ABS,
        ecodes.ABS_HAT0X,
        dpad["x"]
    )

    gamepad.write(
        ecodes.EV_ABS,
        ecodes.ABS_HAT0Y,
        dpad["y"]
    )

    # Submit the complete controller update
    gamepad.syn()


# -----------------------------
# Release everything
# -----------------------------

def release_all(gamepad):

    for code in BUTTON_MAP.values():
        gamepad.write(ecodes.EV_KEY, code, 0)

    for axis in [
        ecodes.ABS_X,
        ecodes.ABS_Y,
        ecodes.ABS_RX,
        ecodes.ABS_RY,
        ecodes.ABS_Z,
        ecodes.ABS_RZ,
        ecodes.ABS_HAT0X,
        ecodes.ABS_HAT0Y
    ]:

        gamepad.write(ecodes.EV_ABS, axis, 0)

    gamepad.syn()


# -----------------------------
# Main server
# -----------------------------

def main():

    gamepad = create_gamepad()

    server = socket.socket(
        socket.AF_INET,
        socket.SOCK_DGRAM
    )

    server.bind((HOST, PORT))
    server.settimeout(PACKET_TIMEOUT)

    print(f"UDP server listening on port {PORT}")
    print("Waiting for controller packets...")

    last_packet_time = time.monotonic()

    try:

        while True:

            try:

                data, address = server.recvfrom(2048)

                state = parse_state(data)

                apply_state(gamepad, state)

                last_packet_time = time.monotonic()

            except socket.timeout:

                if time.monotonic() - last_packet_time >= PACKET_TIMEOUT:

                    release_all(gamepad)

            except (
                ValueError,
                KeyError,
                TypeError,
                UnicodeDecodeError,
                json.JSONDecodeError
            ) as error:

                print("Invalid packet:", error)

    except KeyboardInterrupt:

        print("\nStopping server...")

    finally:

        release_all(gamepad)

        server.close()
        gamepad.close()

        print("Virtual gamepad released.")


if __name__ == "__main__":
    main()
