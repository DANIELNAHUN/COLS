# COLS

Calls-only launcher for seniors — Android.

## Development environment

COLS builds exclusively through the committed Gradle wrapper (`gradlew` /
`gradlew.bat`); no standalone Gradle installation is needed or allowed as a
build entry point. Follow the steps below on a clean machine to reach a first
successful build.

### 1. JDK 17

Install a JDK 17 LTS distribution (Eclipse Temurin is the tested reference:

```powershell
# Windows: winget (or download from https://adoptium.net)
winget install EclipseAdoptium.Temurin.17.JDK
```

Verify from a fresh shell:

```powershell
java -version   # must report 17.x
```

CI pins the same version (`actions/setup-java`, temurin 17) — the JDK is not
upgradeable independently of `gradle/libs.versions.toml` (`jvm = "17"`) and
`.github/workflows/ci.yml`, which must be updated together.

### 2. Android SDK

Install the Android SDK command-line tools, then use `sdkmanager` to install
exactly the components the project pins:

```powershell
# a. Download the command-line tools zip from https://developer.android.com/studio
#    (section "Command line tools only") and unzip it to a stable path, e.g.
#    %LOCALAPPDATA%\Android\Sdk\cmdline-tools\latest
# b. Accept all licenses
sdkmanager --licenses
# c. Install the pinned components (see gradle/libs.versions.toml)
sdkmanager "platform-tools" "platforms;android-37" "build-tools;36.0.0"
```

Verify:

```powershell
sdkmanager --list_installed
```

### 3. ANDROID_HOME

Point the environment at the SDK root:

```powershell
# Windows (persistent, user scope)
[Environment]::SetEnvironmentVariable("ANDROID_HOME", "C:\Users\<you>\AppData\Local\Android\Sdk", "User")
# Linux/macOS: add to your shell profile
# export ANDROID_HOME=$HOME/Android/Sdk
```

CI does not need this step: `.github/workflows/ci.yml` provisions its own SDK
in-job (command-line tools + licenses + the pinned components).

Do NOT commit `local.properties`; it is machine-local and gitignored. On
machines where `ANDROID_HOME` is set, AGP resolves the SDK from it.

### 4. First build and tests

From the repository root:

```powershell
# Windows
.\gradlew.bat :app:assembleDebug      # produces app/build/outputs/apk/debug/app-debug.apk
.\gradlew.bat :app:testDebugUnitTest  # full JVM unit suite (JUnit4 + Robolectric + Compose tests)
```

```bash
# Linux/macOS / CI
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

Additional quality gates used by CI (`.github/workflows/ci.yml`, `pr-gate`):

```powershell
.\gradlew.bat spotlessCheck          # ktlint format check
.\gradlew.bat :app:lintDebug         # Android Lint
.\gradlew.bat :app:koverVerify       # domain-package coverage floor (>=80% line)
```

### 5. Toolchain pins

All toolchain versions live in one version catalog, `gradle/libs.versions.toml`
(AGP 9.2.1, Gradle wrapper 9.4.1, JDK 17, Compose BOM 2026.08.00, compileSdk
37, targetSdk 36, minSdk 26 — each pin carries a verification-date comment).
The committed wrapper downloads Gradle 9.4.1 automatically on first use; a
network connection is required the first time. Fix a broken pin in the catalog,
never in individual build files.
