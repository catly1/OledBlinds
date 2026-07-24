# Update Project to Android 17 (API 37)

This plan outlines the steps to upgrade the OledBlinds project to target the latest stable Android SDK version, API 37 (Android 17), using stable build tools to ensure maximum reliability.

## Proposed Changes

### Build Configuration

#### [MODIFY] [build.gradle (root)](file:///C:/Personal%20Projects/OledBlinds/build.gradle)
- Update Android Gradle Plugin (AGP) from `8.7.0` to `8.12.0` (Stable).
- Update Kotlin Gradle Plugin from `1.9.0` to `2.1.0` (Stable).

#### [MODIFY] [app/build.gradle](file:///C:/Personal%20Projects/OledBlinds/app/build.gradle)
- Update `compileSdkVersion` to `37`.
- Update `targetSdkVersion` to `37`.
- Update core dependencies to stable versions:
    - `androidx.core:core-ktx`: `1.13.1` -> `1.15.0`
    - `androidx.appcompat:appcompat`: `1.7.0` -> `1.7.1`
    - `com.google.android.material:material`: `1.12.0` -> `1.13.0`
    - `androidx.constraintlayout:constraintlayout`: `2.2.0` -> `2.2.1`
    - `androidx.navigation:navigation-ui-ktx`: `2.8.2` -> `2.8.7`
    - `androidx.navigation:navigation-fragment-ktx`: `2.8.2` -> `2.8.7`
    - `kotlin-stdlib-jdk7`: `1.9.20` -> `2.1.0`

#### [MODIFY] [gradle-wrapper.properties](file:///C:/Personal%20Projects/OledBlinds/gradle/wrapper/gradle-wrapper.properties)
- Update Gradle distribution from `8.11-milestone-1` to `8.12` (Stable).

### Manifest and Code Changes

#### [MODIFY] [AndroidManifest.xml](file:///C:/Personal%20Projects/OledBlinds/app/src/main/AndroidManifest.xml)
- No immediate changes required.

## Verification Plan

### Automated Tests
- Run `./gradlew clean build` to verify compilation and build.
- Run unit tests: `./gradlew test`.
- Run instrumented tests: `./gradlew connectedAndroidTest`.

### Manual Verification
- Deploy to an Android 17 device/emulator.
- Verify "Floating Window" (black box) overlay functionality.
- Verify Quick Settings Tile toggle.

> [!IMPORTANT]
> **Stability Priority:** This updated plan avoids AGP 9.x to ensure we stay on a stable, non-breaking major version (8.x). We will use AGP **8.12.0** and Gradle **8.12**, which are stable and provide full support for targeting Android 17 (API 37).
