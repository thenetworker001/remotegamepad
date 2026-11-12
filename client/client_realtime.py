import socket
import json
import time
import threading
import math


# -----------------------------
# Configuration
# -----------------------------

SERVER_IP = "127.0.0.1"
SERVER_PORT = 5005

UPDATE_RATE = 60
UPDATE_INTERVAL = 1 / UPDATE_RATE


# -----------------------------
# Controller state
# -----------------------------

controller_state = {
    "buttons": {
        "a": False, "b": False,
        "x": False, "y": False,
        "lb": False, "rb": False,
        "ls": False, "rs": False,
        "start": False, "select": False,
        "guide": False
    },

    "sticks": {
        "lx": 0.0, "ly": 0.0,
        "rx": 0.0, "ry": 0.0
    },

    "triggers": {
        "lt": 0.0, "rt": 0.0
    },

    "dpad": {
        "x": 0, "y": 0
    }
}


client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

state_lock = threading.Lock()
running = threading.Event()
running.set()


# -----------------------------
# Display available commands
# -----------------------------

def show_help():

    print("""
Controller commands:

BUTTONS:
    a down
    a up
    b down
    b up
    lb down
    lb up

STICKS:
    lx 0.5
    ly -0.5
    rx 0.8
    ry 0.0

TRIGGERS:
    lt 0.5
    rt 1.0

D-PAD:
    dpad 1 0
    dpad -1 1
    dpad 0 0

OTHER:
    show
    help
    quit

Stick range:   -1.0 to 1.0
Trigger range:  0.0 to 1.0
D-pad range:   -1, 0, 1
""")


# -----------------------------
# Process user commands
# -----------------------------

def process_command(command):

    parts = command.lower().split()

    if not parts:
        return

    if parts[0] == "help":
        show_help()
        return

    if parts[0] == "show":

        with state_lock:
            print(json.dumps(controller_state, indent=4))

        return

    if parts[0] == "quit":
        running.clear()
        return

    with state_lock:

        # Button commands
        if parts[0] in controller_state["buttons"]:

            if len(parts) != 2 or parts[1] not in ("down", "up"):
                raise ValueError("Use: button down OR button up")

            controller_state["buttons"][parts[0]] = (
                parts[1] == "down"
            )

        # Stick commands
        elif parts[0] in controller_state["sticks"]:

            if len(parts) != 2:
                raise ValueError("Use: lx 0.5")

            value = float(parts[1])

            if not math.isfinite(value) or not -1.0 <= value <= 1.0:
                raise ValueError("Stick range is -1.0 to 1.0")

            controller_state["sticks"][parts[0]] = value

        # Trigger commands
        elif parts[0] in controller_state["triggers"]:

            if len(parts) != 2:
                raise ValueError("Use: lt 0.5")

            value = float(parts[1])

            if not math.isfinite(value) or not 0.0 <= value <= 1.0:
                raise ValueError("Trigger range is 0.0 to 1.0")

            controller_state["triggers"][parts[0]] = value

        # D-pad commands
        elif parts[0] == "dpad":

            if len(parts) != 3:
                raise ValueError("Use: dpad X Y")

            x = int(parts[1])
            y = int(parts[2])

            if x not in (-1, 0, 1) or y not in (-1, 0, 1):
                raise ValueError("D-pad values must be -1, 0, or 1")

            controller_state["dpad"]["x"] = x
            controller_state["dpad"]["y"] = y

        else:
            raise ValueError("Unknown command. Type help.")


# -----------------------------
# Terminal input thread
# -----------------------------

def input_loop():

    show_help()

    while running.is_set():

        try:
            command = input("\nController > ")
            process_command(command)

        except (ValueError, IndexError) as error:
            print("Error:", error)

        except EOFError:
            running.clear()


# -----------------------------
# Main sending loop
# -----------------------------

def main():

    input_thread = threading.Thread(
        target=input_loop,
        daemon=True
    )

    input_thread.start()

    print("\nSending controller states at 60 Hz.")

    try:

        while running.is_set():

            start_time = time.perf_counter()

            with state_lock:
                packet = json.dumps(controller_state).encode("utf-8")

            client.sendto(
                packet,
                (SERVER_IP, SERVER_PORT)
            )

            elapsed = time.perf_counter() - start_time

            sleep_time = UPDATE_INTERVAL - elapsed

            if sleep_time > 0:
                time.sleep(sleep_time)

    except KeyboardInterrupt:
        print("\nStopping client...")

    finally:

        # Send a neutral state before disconnecting
        with state_lock:

            for name in controller_state["buttons"]:
                controller_state["buttons"][name] = False

            for name in controller_state["sticks"]:
                controller_state["sticks"][name] = 0.0

            for name in controller_state["triggers"]:
                controller_state["triggers"][name] = 0.0

            controller_state["dpad"] = {"x": 0, "y": 0}

            packet = json.dumps(controller_state).encode("utf-8")

        try:
            client.sendto(packet, (SERVER_IP, SERVER_PORT))
        except OSError:
            pass

        client.close()

        print("Client stopped.")


if __name__ == "__main__":
    main()
