# lib-librespot-android
[![](https://jitpack.io/v/capullo-tech/lib-librespot-android.svg)](https://jitpack.io/#capullo-tech/lib-librespot-android)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/guide/)
![API](https://img.shields.io/badge/Min%20API-23-green)
![API](https://img.shields.io/badge/Compiled%20API-36-green)

This library packages an Android-ready bundle of [librespot-java](https://github.com/librespot-org/librespot-java) plus the Android adapter modules from [librespot-android](https://github.com/devgianlu/librespot-android) and [librespot-connect-android](https://github.com/powerbling/librespot-connect-android):

- **librespot-android** — vendored sources from the [capullo-tech/librespot-java](https://github.com/capullo-tech/librespot-java) fork (`dev-jsp` branch). Provides the librespot core (`Session`, `Player`, mercury, dealer, metadata, proto-generated types) as a single Android library. Replaces depending on `xyz.gianlu.librespot:librespot-player` from Maven.
- [librespot-android-decoder](https://github.com/devgianlu/librespot-android/tree/master/librespot-android-decoder) — `AndroidNativeDecoder` (MediaCodec-backed Vorbis/MP3)
- [librespot-android-decoder-tremolo](https://github.com/devgianlu/librespot-android/tree/master/librespot-android-decoder-tremolo) — ARM-optimized Tremolo Vorbis decoder (ships native `.so`)
- [librespot-android-sink](https://github.com/devgianlu/librespot-android/tree/master/librespot-android-sink) — `AndroidSinkOutput` (AudioTrack-backed sink)
- [librespot-android-zeroconf-server](https://github.com/powerbling/librespot-connect-android/tree/master/librespot-android-zeroconf-server) — Spotify Connect zeroconf discovery for Android

The 4 adapter modules transitively pull in `librespot-android` (via `api`), so a consumer only needs to depend on whichever adapters they use.

# Installation

Published via [JitPack](https://jitpack.io/#capullo-tech/lib-librespot-android). Each module is a separate artifact under the `com.github.capullo-tech.lib-librespot-android` group.

1. Add the JitPack repository to your settings (or root `build.gradle`):
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```
2. Add the modules you need. Most apps want the decoder, sink, and zeroconf adapters (which transitively bring in `librespot-android`):
```kotlin
dependencies {
    implementation("com.github.capullo-tech.lib-librespot-android:librespot-android-decoder:0.2.0-rc02")
    implementation("com.github.capullo-tech.lib-librespot-android:librespot-android-sink:0.2.0-rc02")
    implementation("com.github.capullo-tech.lib-librespot-android:librespot-android-zeroconf-server:0.2.0-rc02")
    // Optional: ARM-optimized Tremolo Vorbis decoder
    implementation("com.github.capullo-tech.lib-librespot-android:librespot-android-decoder-tremolo:0.2.0-rc02")
    // If you only need the librespot core (Session, Player) without adapters:
    // implementation("com.github.capullo-tech.lib-librespot-android:librespot-android:0.2.0-rc02")
}
```

# Usage
Register the Android Native Decoders at app startup
```java
import xyz.gianlu.librespot.audio.decoders.Decoders;
import xyz.gianlu.librespot.audio.format.SuperAudioFormat;
import xyz.gianlu.librespot.player.decoders.AndroidNativeDecoder;
import xyz.gianlu.librespot.player.decoders.TremoloVorbisDecoder;

public final class LibrespotApp extends Application {
    static {
        Decoders.registerDecoder(SuperAudioFormat.VORBIS, 0, AndroidNativeDecoder.class);
        Decoders.registerDecoder(SuperAudioFormat.MP3, 0, AndroidNativeDecoder.class);

        if (isArm()) {
            // Using ARM optimized Vorbis decoder
            Decoders.registerDecoder(SuperAudioFormat.VORBIS, 0, TremoloVorbisDecoder.class);
        }
    }

    private static boolean isArm() {
        for (String abi : Build.SUPPORTED_ABIS)
            if (abi.contains("arm"))
                return true;

        return false;
    }
}
```

Create a session and a Player. The `Session.Builder.create()` call throws `TokenProvider.TokenException` (replaced the old `MercuryClient.MercuryException`) — make sure to catch it.
```java
import xyz.gianlu.librespot.android.sink.AndroidSinkOutput;
import xyz.gianlu.librespot.core.Session;
import xyz.gianlu.librespot.core.TokenProvider;
import xyz.gianlu.librespot.player.Player;
import xyz.gianlu.librespot.player.PlayerConfiguration;

public final class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Session.Configuration conf = new Session.Configuration.Builder()
                    .setStoreCredentials(true)
                    .setStoredCredentialsFile(new File(getFilesDir(), "credentials.json"))
                    .setCacheEnabled(false)
                    .build();

            Session.Builder builder = new Session.Builder(conf)
                    .setPreferredLocale(Locale.getDefault().getLanguage())
                    .setDeviceType(Connect.DeviceType.SMARTPHONE)
                    .setDeviceId(null)
                    .setDeviceName("librespot-android");

            Session session = builder.userPass("<username>", "<password>").create();

            PlayerConfiguration configuration = new PlayerConfiguration.Builder()
                    .setOutput(PlayerConfiguration.AudioOutput.CUSTOM)
                    .setOutputClass(AndroidSinkOutput.class.getName())
                    .build();

            Player player = new Player(configuration, session);
        } catch (IOException | GeneralSecurityException |
                 Session.SpotifyAuthenticationException |
                 TokenProvider.TokenException ex) {
            // handle login failure
        }
    }
}
```

See the [demo app](app) for the full canonical setup, including credentials-file persistence and zeroconf-server integration.

# Credits
- [librespot-java](https://github.com/librespot-org/librespot-java) — the upstream Java implementation; vendored from the [capullo-tech](https://github.com/capullo-tech/librespot-java) fork
- [librespot-android](https://github.com/devgianlu/librespot-android) — origin of the decoder and sink adapter modules
- [librespot-connect-android](https://github.com/powerbling/librespot-connect-android) — origin of the zeroconf-server adapter

# Demo app

The `app` module is a minimal demonstration that runs librespot on Android — login (username/password), play a custom URI, pause/resume, skip. Useful as a reference for wiring the library into a real app.

# License

[Apache License 2.0](https://choosealicense.com/licenses/apache-2.0/)
