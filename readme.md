# Jogjegyzet Android

This is an Android client for the service https://jogjegyzet.hu

## Architecture

The project is based on the MVP design pattern. For navigation and UI composition it uses [Conductor](https://github.com/bluelinelabs/Conductor).

The components used, in a nutshell:

* Kotlin as the programming language
* Dagger 2 for Dependency Injection
* Room for persistence
* Firebase for crash reporting and analytics