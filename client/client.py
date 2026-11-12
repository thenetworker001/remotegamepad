import socket
import json
import time


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
        "a": True,
        "b": False,
        "x": False,
        "y": False,

        "lb": False,
        "rb": False,

        "ls": False,
        "rs": False,

        "start": False,
        "select": False,
        "guide": False
    },

    "sticks": {
        "lx": 0.5,
        "ly": -0.2,

        "rx": 0.0,
        "ry": 0.0
    },

    "triggers": {
        "lt": 0.8,
        "rt": 0.0
    },

    "dpad": {
        "x": 1,
        "y": -1
    }
}


# -----------------------------
# UDP client
# -----------------------------

client = socket.socket(
    socket.AF_INET,
    socket.SOCK_DGRAM
)


def main():

    print("Continuous controller client started.")
    print(f"Sending at {UPDATE_RATE} Hz.")
    print("Press Ctrl+C to stop.")

    try:

        while True:

            start_time = time.perf_counter()

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

        client.close()


if __name__ == "__main__":
    main()
