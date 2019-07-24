[![Build Status](https://api.travis-ci.org/jogjegyzet/jogjegyzet-android.svg?branch=master)](https://travis-ci.org/jogjegyzet/jogjegyzet-android)

# Jogjegyzet Android

This is an Android client for the service https://jogjegyzet.hu

## Architecture

The project is based on the MVP design pattern. For navigation and UI composition it uses [Conductor](https://github.com/bluelinelabs/Conductor).

The components used, in a nutshell:

* Kotlin as the programming language
* Dagger 2 for Dependency Injection
* Room for persistence
* Firebase for crash reporting and analytics

<a href="https://play.google.com/store/apps/details?id=com.danielgergely.jogjegyzet"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" height=60px /></a>
