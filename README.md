# Linux Virtual Gamepad Controller

A personal project to turn an Android device into a wireless virtual gamepad controller for Linux.

The idea is simple: use an Android phone as a gamepad and send its button inputs to a Linux machine, where they are translated into virtual gamepad inputs using Linux's `uinput` interface.

The project is currently in early development. The initial focus is on getting the Linux-side virtual gamepad and Android-to-Linux communication working reliably.

## Overview

This project aims to create a simple Android application that can act as a gamepad controller for a Linux system.

The Android application will provide a basic gamepad-style interface. Button inputs will be sent over a local network using UDP to a Python server running on Linux.

The Python server will process the received commands and use Linux's `uinput` interface to simulate gamepad button inputs, making the virtual controller available to applications and games that support gamepads.

## How It Works

The planned communication flow is:

```text
      Android Device
   ┌──────────────────┐
   │                  │
   │  Android App     │
   │  (Kotlin)        │
   │                  │
   │  Gamepad UI      │
   │       │          │
   │       ▼          │
   │  UDP Commands    │
   │                  │
   └────────┬─────────┘
            │
            │ Local Network
            │ UDP
            ▼
   ┌──────────────────┐
   │   Linux System   │
   │                  │
   │  Python Server   │
   │       │          │
   │       ▼          │
   │  Linux uinput    │
   │       │          │
   │       ▼          │
   │  Virtual Gamepad │
   │                  │
   └──────────────────┘
```

## Current Development Status

**Status: Early Beta / Active Development**

The project is being developed and tested on Linux, with Android as the intended controller device.

### Linux Side

- [x] Explored and tested Linux virtual gamepad functionality.
- [x] Created a Python script to interact with Linux `uinput`.
- [x] Successfully tested virtual gamepad button inputs using commands.
- [x] Created a Python UDP server to receive commands.
- [x] Integrated the UDP server with `uinput`.
- [x] Tested command transmission and virtual button inputs over localhost.
- [ ] Complete and refine the Linux server implementation.
- [ ] Test communication between an actual Android device and the Linux server.

### Android Side

- [x] Started Android application development using Kotlin.
- [x] Created an initial UI with a basic button.
- [ ] Develop the complete gamepad controller layout.
- [ ] Implement UDP communication with the Linux server.
- [ ] Connect UI button interactions to controller commands.
- [ ] Test the complete Android-to-Linux workflow.

## Technology Stack

| Component | Technology |
|---|---|
| Android Application | Kotlin |
| Android UI | Android UI toolkit (under development) |
| Linux Backend | Python |
| Virtual Gamepad | Linux `uinput` |
| Communication | UDP |
| Development Platform | Linux |

## Platform Support

| Platform | Status |
|---|---|
| Linux | Primary development and target platform |
| Android | Controller application under development |
| Windows | Possible future expansion |

Currently, the project focuses exclusively on Linux as the host system and Android as the controller device.

Windows support is only a possible future direction and is not currently implemented.

## Project Goals

The main goal is to build a functional and lightweight virtual gamepad controller that allows an Android device to control games and applications on a Linux machine.

Some potential improvements for the future include:

- Complete and refine the Android gamepad interface.
- Establish reliable communication between Android and Linux.
- Add support for analog controls such as joysticks and triggers.
- Improve input handling and responsiveness.
- Explore support for additional host platforms, potentially including Windows.

These are development goals, not currently implemented features.

## Development Notes

This project is primarily being developed as a personal learning and practical-use project.

It is not currently intended to be a production-ready or commercial application. The focus is on understanding the underlying technologies, implementing the communication between Android and Linux, and building something that works reliably for personal use.

Features and implementation details may change as development progresses.

## License

License not decided yet.
