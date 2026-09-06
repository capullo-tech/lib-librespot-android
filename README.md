# lib-librespot-android
[![](https://jitpack.io/v/capullo-tech/lib-librespot-android.svg)](https://jitpack.io/#capullo-tech/lib-librespot-android)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/guide/)
![API](https://img.shields.io/badge/Min%20API-23-green)
![API](https://img.shields.io/badge/Compiled%20API-36-green)

An Android-ready packaging of the [librespot-java](https://github.com/librespot-org/librespot-java) core — the open-source Spotify client library — as a single Android library module. Provides `Session`, `Player`, mercury, dealer, the audio pipeline (CDN fetch, AES audio-key decryption, Vorbis/MP3 decoding), metadata types, and the `com.spotify.*` protobuf-generated classes.

This repo is exactly one library module at its root: it opens directly in Android Studio, publishes to Maven via `maven-publish`, and can be dropped into another Gradle build as a git submodule (`include(":lib-librespot-android")`).

## Provenance

Snapshot of [capullo-tech/librespot-java](https://github.com/capullo-tech/librespot-java), branch `dev-jsp`, commit `5eee2170`. The upstream Maven modules `lib`, `player`, `decoder-api`, `sink-api`, and `dacp` were flattened into this single module; the desktop-only `sink` and `api` modules, tests, and Maven scaffolding were dropped.

Divergences from upstream `dev-jsp`:

- `player/state/DeviceStateHandler.java`: tolerate a null `connectionId` in `updateState` (skip the put-state update instead of throwing `IllegalStateException`) — hardens against a dropped `connection_id` dealer message racing player startup.
- CLI-only classes removed: `ZeroconfServer`, `player/Main`, `player/FileConfiguration`, `player/ShellEvents`, plus `default.toml` / `log4j2.xml` resources. Android apps drive `Session`/`Player` programmatically; the `com.sun.net.httpserver` compile stubs remain because `core/OAuth` (referenced by `Session`) needs them.

## Installation (JitPack)

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

dependencies {
    implementation("com.github.capullo-tech.lib-librespot-android:librespot-android:0.3.0")
}
```

The AAR is published with a sources jar. Releases before `0.3.0` were published from a multi-module layout that also contained Android adapter modules (`librespot-android-sink`, `-decoder`, `-decoder-tremolo`, `-zeroconf-server`); those tags remain available on JitPack but the adapters are no longer maintained here — most apps are better served implementing the `SinkOutput` / `Decoder` SPIs directly (see `player.mixing.output.SinkOutput` and `player.decoders.Decoder`).

## Usage

```java
Session.Configuration conf = new Session.Configuration.Builder()
        .setStoreCredentials(true)
        .setStoredCredentialsFile(new File(getFilesDir(), "credentials.json"))
        .setCacheEnabled(false)
        .build();

Session session = new Session.Builder(conf)
        .setPreferredLocale(Locale.getDefault().getLanguage())
        .setDeviceType(Connect.DeviceType.SMARTPHONE)
        .setDeviceName("my-app")
        .blob(username, decryptedBlob) // Spotify Connect zeroconf pairing
        .create();

PlayerConfiguration configuration = new PlayerConfiguration.Builder()
        .setOutput(PlayerConfiguration.AudioOutput.CUSTOM)
        .setOutputClass(MySinkOutput.class.getName()) // your SinkOutput implementation
        .build();

Player player = new Player(configuration, session);
```

For a full production integration (zeroconf pairing via NSD, `AudioTrack` sink, `MediaCodec` decoder, androidx.media2 bridge), see [Jetispot](https://github.com/capullo-tech/Jetispot), which consumes this repo as a git submodule.

## Credits

- [librespot-java](https://github.com/librespot-org/librespot-java) — the upstream Java implementation (devgianlu), vendored from the [capullo-tech](https://github.com/capullo-tech/librespot-java) fork

## License

[Apache License 2.0](LICENSE)
