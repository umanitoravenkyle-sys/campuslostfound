# Implementation Plan - Fix and Connect Campus Lost & Found App

The current state of the app has several issues:
1.  **Compilation Error**: `MainActivity.kt` refers to `LostItemScreen()` which should be `LostItemsScreen()`.
2.  **Package Mismatch**: `FoundItemsScreen.kt` uses an incorrect package name (`com.example.campuslostandfound`).
3.  **Code Duplication**: The brand color `PLMUNGreen` is defined locally in every screen file.
4.  **Missing Navigation**: Multiple screens exist (`Login`, `SignUp`, `Home`, `LostItems`, `FoundItems`) but are not connected.

## Proposed Changes

### [Build Configuration]

#### [MODIFY] [libs.versions.toml](file:///D:/Campus%20Lost%20&%20Found-20260903T013411Z-1-001/Campus%20Lost%20&%20Found/gradle/libs.versions.toml)
- Add `navigationCompose = "2.10.0"` to `[versions]`.
- Add `androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }` to `[libraries]`.

#### [MODIFY] [build.gradle.kts](file:///D:/Campus%20Lost%20&%20Found-20260903T013411Z-1-001/Campus%20Lost%20&%20Found/app/build.gradle.kts)
- Add `implementation(libs.androidx.navigation.compose)` to `dependencies`.

### [Theme & Colors]

#### [MODIFY] [Color.kt](file:///D:/Campus%20Lost%20&%20Found-20260903T013411Z-1-001/Campus%20Lost%20&%20Found/app/src/main/java/com/example/campuslostfound/ui/theme/Color.kt)
- Add `PLMUNGreen` definition.

#### [MODIFY] [Theme.kt](file:///D:/Campus%20Lost%20&%20Found-20260903T013411Z-1-001/Campus%20Lost%20&%20Found/app/src/main/java/com/example/campuslostfound/ui/theme/Theme.kt)
- Set `PLMUNGreen` as the primary color in both `LightColorScheme` and `DarkColorScheme`.

### [Screens Cleanup]

#### [MODIFY] [LostItemsScreen.kt](file:///D:/Campus%20Lost%20&%20Found-20260903T013411Z-1-001/Campus%20Lost%20&%20Found/app/src/main/java/com/example/campuslostfound/LostItemsScreen.kt)
- Remove local `PLMUNGreen` definition.
- Import `PLMUNGreen` from `ui.theme`.

#### [MODIFY] [FoundItemsScreen.kt](file:///D:/Campus%20Lost%20&%20Found-20260903T013411Z-1-001/Campus%20Lost%20&%20Found/app/src/main/java/com/example/campuslostfound/FoundItemsScreen.kt)
- Fix package name to `com.example.campuslostfound`.
- Remove local `PLMUNGreen` definition.
- Import `PLMUNGreen` from `ui.theme`.

#### [MODIFY] [HomeScreen.kt](file:///D:/Campus%20Lost%20&%20Found-20260903T013411Z-1-001/Campus%20Lost%20&%20Found/app/src/main/java/com/example/campuslostfound/HomeScreen.kt)
- Remove local `PLMUNGreen` definition.
- Import `PLMUNGreen` from `ui.theme`.
- Add navigation callbacks for "Lost Items" and "Found Items" cards.

#### [MODIFY] [LoginScreen.kt](file:///D:/Campus%20Lost%20&%20Found-20260903T013411Z-1-001/Campus%20Lost%20&%20Found/app/src/main/java/com/example/campuslostfound/LoginScreen.kt)
- Remove local `PLMUNGreen` definition.
- Import `PLMUNGreen` from `ui.theme`.

#### [MODIFY] [SignUpScreen.kt](file:///D:/Campus%20Lost%20&%20Found-20260903T013411Z-1-001/Campus%20Lost%20&%20Found/app/src/main/java/com/example/campuslostfound/SignUpScreen.kt)
- Remove local `PLMUNGreen` definition.
- Import `PLMUNGreen` from `ui.theme`.

### [App Navigation]

#### [MODIFY] [MainActivity.kt](file:///D:/Campus%20Lost%20&%20Found-20260903T013411Z-1-001/Campus%20Lost%20&%20Found/app/src/main/java/com/example/campuslostfound/MainActivity.kt)
- Add `androidx.navigation:navigation-compose` dependency (check `build.gradle` first).
- Implement `NavHost` to manage all screens.
- Fix the `LostItemScreen` typo.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to ensure no compilation errors.

### Manual Verification
- Deploy to device/emulator.
- Verify that the app starts at the Login screen.
- Verify navigation: Login -> Home -> Lost Items / Found Items.
- Verify navigation: Login -> Sign Up -> Login.
