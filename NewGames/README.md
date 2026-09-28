# NewGames

`NewGames` is the modern Compose rewrite of the legacy `Games` Android app.

## Highlights

- Kotlin + Jetpack Compose + Material 3
- Navigation Compose with a single activity
- ViewModel + StateFlow for game state
- Bundled local game content from `app/src/main/assets`
- No advertising, analytics, update SDK, Pangle, or Pgyer integration
- Legacy dark-purple gradient and colorful rounded game cards preserved

## Open and run

Open the `NewGames` directory in Android Studio 2025.2.1 or newer, let Gradle sync, then run the `app` configuration.

From a terminal with the Android SDK configured:

```powershell
./gradlew.bat :app:assembleDebug
./gradlew.bat :app:testDebugUnitTest
```

The generated debug APK is placed under `app/build/outputs/apk/debug/`.

On Windows, double-click `build-apk.bat` to build the signed Release APK with the local `keystore.properties` and `app.jks`. The versioned APK is saved as `app/build/outputs/apk/release/NewGames-v<versionName>.apk` (for example, `NewGames-v1.0.0.apk`); Gradle's original `app-release.apk` remains in the same folder. From a terminal, use `build-apk.bat nopause` to skip the final pause. The signing files are excluded from Git.
