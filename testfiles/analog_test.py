from evdev import UInput, ecodes, AbsInfo
import time

capabilities = {
    ecodes.EV_KEY: [
        ecodes.BTN_SOUTH,
        ecodes.BTN_EAST,
    ],

    ecodes.EV_ABS: [
        (ecodes.ABS_X, AbsInfo(0, -32768, 32767, 0, 0, 0)),
        (ecodes.ABS_Y, AbsInfo(0, -32768, 32767, 0, 0, 0)),
        (ecodes.ABS_RX, AbsInfo(0, -32768, 32767, 0, 0, 0)),
        (ecodes.ABS_RY, AbsInfo(0, -32768, 32767, 0, 0, 0)),
    ]
}

gamepad = UInput(
    capabilities,
    name="Stark Virtual Gamepad"
)

print("Virtual gamepad initialized!")

try:
    while True:

        print("Center position")

        gamepad.write(ecodes.EV_ABS, ecodes.ABS_X, 0)
        gamepad.write(ecodes.EV_ABS, ecodes.ABS_Y, 0)
        gamepad.syn()

        time.sleep(1)

        print("Moving left")

        gamepad.write(ecodes.EV_ABS, ecodes.ABS_X, -32768)
        gamepad.syn()

        time.sleep(1)

        print("Moving right")

        gamepad.write(ecodes.EV_ABS, ecodes.ABS_X, 32767)
        gamepad.syn()

        time.sleep(1)

        print("Moving up")

        gamepad.write(ecodes.EV_ABS, ecodes.ABS_Y, -32768)
        gamepad.syn()

        time.sleep(1)

        print("Moving down")

        gamepad.write(ecodes.EV_ABS, ecodes.ABS_Y, 32767)
        gamepad.syn()

        time.sleep(1)

except KeyboardInterrupt:
    gamepad.close()
    print("Controller closed.")
