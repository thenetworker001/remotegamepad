import socket
import json

# -----------------------------
# SERVER CONFIGURATION
# -----------------------------

SERVER_IP = "127.0.0.1"
SERVER_PORT = 5005

# -----------------------------
# CREATE UDP SOCKET
# -----------------------------

client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# -----------------------------
# CONTROLLER STATE
# -----------------------------

controller_state = {
    "buttons": {
        "a": True,
        "b": False,
        "x": False,
        "y": False,
        "lb": True,
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
# CONVERT STATE TO JSON
# -----------------------------

message = json.dumps(controller_state)

# Convert JSON string into bytes
data = message.encode("utf-8")

# -----------------------------
# SEND UDP PACKET
# -----------------------------

client.sendto(data, (SERVER_IP, SERVER_PORT))

print("Controller state sent!")
print(message)

client.close()
