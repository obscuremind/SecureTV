# SecureTV - Android IPTV Player

A secure, production-grade Android IPTV Player application using Kotlin and Android Studio.

## Build Status

[![Build Status](https://github.com/bridgyplayer/SecureTV/actions/workflows/android.yml/badge.svg)](https://github.com/bridgyplayer/SecureTV/actions/workflows/android.yml)

## Features

- 🔐 Secure login system (Xtream Codes compatible)
- 🌐 DNS obfuscation via reverse proxy
- 🔥 Firebase integration for remote configuration
- 🎥 Advanced video player with ExoPlayer
- 🖼️ Modern Netflix-style UI with Jetpack Compose
- 🧪 Network traffic masking for privacy
- 🛠️ MVVM architecture

## Security Features

- Encrypted credential storage
- Traffic routed through proxy to hide real DNS
- Secure network communication

## Technology Stack

- Android (Kotlin)
- Firebase Remote Config
- Retrofit + OkHttp
- ExoPlayer
- Jetpack Compose UI
- MVVM architecture

## Setup Instructions

Please refer to the [SETUP.md](SETUP.md) file for detailed setup instructions.

## Troubleshooting

If you encounter build issues:

1. Make sure you're using Java 8 compatibility settings
2. Check that all JVM targets are set to 1.8
3. Ensure you have the correct Gradle and Android Gradle Plugin versions (7.5 and 7.4.2 respectively)
4. See [SETUP.md](SETUP.md) for more detailed troubleshooting steps
