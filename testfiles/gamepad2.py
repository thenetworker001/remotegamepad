from evdev import UInput, ecodes
import time

gamepad = UInput({
    ecodes.EV_KEY: [
        ecodes.BTN_SOUTH,
        ecodes.BTN_EAST,
    ]
}, name="Stark Virtual Gamepad")

print("Virtual gamepad ready!")

try:
    while True:
        print("Pressing A")

        gamepad.write(ecodes.EV_KEY, ecodes.BTN_SOUTH, 1)
        gamepad.syn()

        time.sleep(1)

        print("Releasing A")

        gamepad.write(ecodes.EV_KEY, ecodes.BTN_SOUTH, 0)
        gamepad.syn()

        time.sleep(1)

except KeyboardInterrupt:
    print("Closing controller...")
    gamepad.close()
