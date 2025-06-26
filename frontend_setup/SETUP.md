# EventPlanner Android Setup Guide

## 1. Install Required Software

### Android Studio Installation
1. Download Android Studio from [official website](https://developer.android.com/studio)
2. Run the installer and follow the setup wizard
3. During installation, make sure to select:
   - Android SDK
   - Android Virtual Device
   - Performance (Intel HAXM)
   - Android Emulator

### JDK Installation
1. Download JDK 11 or higher from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
2. Install JDK and set JAVA_HOME environment variable
3. Verify installation:
   ```bash
   java -version
   ```

## 2. Configure Android Studio

### Initial Setup
1. Launch Android Studio
2. Complete the initial setup wizard
3. Install recommended plugins:
   - Kotlin
   - Android SDK Tools
   - Android SDK Platform-Tools
   - Android Emulator
   - Android SDK Build-Tools

### SDK Installation
1. Open SDK Manager (Tools > SDK Manager)
2. Install the following:
   - Android SDK Platform 34 (Android 14)
   - Android SDK Platform 24 (Android 7.0)
   - Android SDK Build-Tools
   - Android SDK Command-line Tools
   - Android Emulator
   - Android SDK Platform-Tools

## 3. Project Setup

### Clone Repository
```bash
git clone https://github.com/yourusername/EventPlanner-Android.git
```

### Open Project
1. Launch Android Studio
2. Select "Open an existing project"
3. Navigate to the cloned EventPlanner-Android directory
4. Click "OK"

### Configure Gradle
1. Wait for Gradle sync to complete
2. If prompted, update Gradle version
3. Install any missing dependencies

## 4. Development Environment

### Configure Emulator
1. Open AVD Manager (Tools > AVD Manager)
2. Click "Create Virtual Device"
3. Select a phone (e.g., Pixel 4)
4. Download and select system image (API 34 recommended)
5. Complete the setup and launch the emulator

### Configure Real Device
1. Enable Developer Options on your Android device:
   - Go to Settings > About Phone
   - Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Go to Settings > Developer Options
   - Enable "USB Debugging"
3. Connect device via USB
4. Accept debugging authorization on device

## 5. Build and Run

### First Build
1. Click "Build > Clean Project"
2. Click "Build > Rebuild Project"
3. Wait for build to complete

### Run Application
1. Select your device/emulator from the toolbar
2. Click "Run" (green play button)
3. Wait for app to install and launch

## 6. Common Issues and Solutions

### Build Errors
- Clean and rebuild project
- Invalidate caches (File > Invalidate Caches)
- Update Gradle version
- Check SDK installation

### Emulator Issues
- Enable virtualization in BIOS
- Update graphics drivers
- Increase emulator RAM in AVD settings

### Device Connection Issues
- Update USB drivers
- Try different USB cable
- Enable USB debugging
- Check device compatibility

## 7. Development Tools

### Recommended Plugins
- Kotlin
- Android WiFi ADB
- Material Design Icon Generator
- JSON To Kotlin Class
- Android Drawable Preview

### Useful Shortcuts
- `Ctrl + Space`: Code completion
- `Alt + Enter`: Quick fixes
- `Ctrl + Alt + L`: Format code
- `Shift + F10`: Run app
- `Ctrl + F9`: Build project

## 8. Next Steps

### Start Development
1. Review project structure
2. Set up API endpoints
3. Configure signing keys
4. Start implementing features

### Testing
1. Set up unit tests
2. Configure UI tests
3. Test on multiple devices

## Support
For technical support:
- Email: [Your Support Email]
- GitHub Issues: [Repository Issues]
- Stack Overflow: [Your Stack Overflow Tag] 