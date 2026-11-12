import socket

# Network configuration
HOST = "0.0.0.0"
PORT = 5005

# Create a UDP socket
server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# Listen on the selected port
server.bind((HOST, PORT))

print("UDP receiver started!")
print(f"Listening on port {PORT}...")
print("Waiting for messages...\n")

try:
    while True:
        # Receive a message (maximum 1024 bytes)
        data, address = server.recvfrom(1024)

        # Convert received bytes into readable text
        message = data.decode("utf-8", errors="replace")

        print(f"Received from {address}: {message}")

except KeyboardInterrupt:
    print("\nShutting down UDP receiver...")

finally:
    server.close()
