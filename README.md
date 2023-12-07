# lib-librespot-android
[![](https://jitpack.io/v/capullo-tech/lib-librespot-android.svg)](https://jitpack.io/#capullo-tech/lib-librespot-android)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/guide/)
![API](https://img.shields.io/badge/Min%20API-23-green)
![API](https://img.shields.io/badge/Compiled%20API-34-green)

This library packages the android modules from [librespot-android](https://github.com/devgianlu/librespot-android) and [librespot-connect-android](https://github.com/powerbling/librespot-connect-android), namely:

- [librespot-android-decoder](https://github.com/devgianlu/librespot-android/tree/master/librespot-android-decoder)
- [librespot-android-decoder-tremolo](https://github.com/devgianlu/librespot-android/tree/master/librespot-android-decoder-tremolo)
- [librespot-android-sink](https://github.com/devgianlu/librespot-android/tree/master/librespot-android-sink)
- [librespot-android-zeroconf-server](https://github.com/powerbling/librespot-connect-android/tree/master/librespot-android-zeroconf-server)

Intended to be used in conjunction with [librespot-java](https://github.com/librespot-org/librespot-java) to make a spotify-connect enabled Android application

# Installation

Obtain it via [jitpack](https://jitpack.io/#gsalinaslopez/lib-librespot-android) using gradle.

1. Add the jitpack repository your root **build.gradle**:
```groovy
repositories {
    maven { url "https://jitpack.io"  }
}
```
2. Add the dependency
```groovy
dependencies {
    implementation 'tech.capullo:lib-librespot-android:0.1.0-rc01'

    // Use together with [librespot-java](https://github.com/librespot-org/librespot-java)
    implementation('xyz.gianlu.librespot:librespot-player:1.6.3:thin') {
        exclude group: 'xyz.gianlu.librespot', module: 'librespot-sink'
        exclude group: 'com.lmax', module: 'disruptor'
        exclude group: 'org.apache.logging.log4j'
    }
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

Create a session and a Player
```java
import xyz.gianlu.librespot.android.sink.AndroidSinkOutput;
import xyz.gianlu.librespot.core.Session;
import xyz.gianlu.librespot.player.Player;
import xyz.gianlu.librespot.player.PlayerConfiguration;

public final class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Session.Configuration conf = new Session.Configuration.Builder()
                .setCacheEnabled()
                .setCacheDir()
                .setDoCacheCleanUp()
                .setStoreCredentials()
                .setStoredCredentialsFile()
                .setTimeSynchronizationMethod()
                .setTimeManualCorrection()
                .setProxyEnabled()
                .setProxyType()
                .setProxyAddress()
                .setProxyPort()
                .setProxyAuth()
                .setProxyUsername()
                .setProxyPassword()
                .setRetryOnChunkError()
                .build();

        Session.Builder builder = new Session.Builder(conf)
                .setPreferredLocale(Locale.getDefault().getLanguage())
                .setDeviceType(Connect.DeviceType.SMARTPHONE)
                .setDeviceId(null)
                .setDeviceName("librespot-android");

        Session session = builder
                .userPass("<username>", "<password>")
                .create();

        PlayerConfiguration configuration = new PlayerConfiguration.Builder()
                .setOutput(PlayerConfiguration.AudioOutput.CUSTOM)
                .setOutputClass(AndroidSinkOutput.class.getName())
                .build();

        Player player = new Player(configuration, session);
    }
}
```

See the [example app](app)

# Credits
- [librespot-android](https://github.com/devgianlu/librespot-android)
- [librespot-connect-android](https://github.com/powerbling/librespot-connect-android)

# librespot-android

This is a demo application to demonstrate that it is possible to run [librespot-java](https://github.com/librespot-org/librespot-java) on an Android device. The app provides basic functionalities to login and then to play a custom URI, pause/resume, skip next and previous, but all features could be implemented. 

This repo also contains some useful modules that contain Android-compatible sinks and decoders that you might want to use in your app.

# License

[Apache License 2.0](https://choosealicense.com/licenses/apache-2.0/)
