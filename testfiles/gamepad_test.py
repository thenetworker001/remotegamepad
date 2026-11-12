from evdev import UInput, ecodes

gamepad = UInput({
    ecodes.EV_KEY: [
        ecodes.BTN_SOUTH,
        ecodes.BTN_EAST,
    ]
}, name="Stark Virtual Gamepad")

print("Virtual gamepad created!")
print("Press Ctrl+C to exit.")

try:
    while True:
        pass
except KeyboardInterrupt:
    gamepad.close()
