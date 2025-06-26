# EventPlanner - Android Application

## Overview
EventPlanner is an Android application that provides a seamless mobile experience for the EventEase platform. It allows users to manage events, participate in activities, and stay connected with event organizers on the go.

## Features
- 📱 Native Android experience
- 🔐 Secure authentication
- 📅 Event browsing and management
- 👤 User profile management
- 📍 Location-based event discovery
- 🔔 Push notifications
- 📸 Image upload support
- 🌐 Offline data caching

## Technical Stack
- **Language**: Kotlin
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Libraries**:
  - Retrofit for API calls
  - Room for local database
  - Coroutines for async operations
  - Dagger Hilt for dependency injection
  - Glide for image loading
  - Material Design components

## Prerequisites
- Android Studio Arctic Fox or newer
- JDK 11 or higher
- Android SDK 24+
- Gradle 7.0+

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/EventPlanner-Android.git
```

### 2. Open in Android Studio
- Launch Android Studio
- Select "Open an existing project"
- Navigate to the cloned repository
- Click "OK"

### 3. Configure API
Update `app/src/main/java/com/eventplanner/utils/Constants.kt`:
```kotlin
object Constants {
    const val BASE_URL = "http://your-api-url/api/"
    const val API_KEY = "your-api-key"
}
```

### 4. Build and Run
- Click "Run" in Android Studio
- Select your device/emulator
- Wait for the app to build and install

## Project Structure
```
app/src/main/
├── java/com/eventplanner/
│   ├── data/           # Data layer
│   │   ├── api/       # API interfaces
│   │   ├── db/        # Room database
│   │   └── repository/# Repository implementations
│   ├── di/            # Dependency injection
│   ├── ui/            # UI components
│   │   ├── auth/      # Authentication screens
│   │   ├── events/    # Event management screens
│   │   └── profile/   # User profile screens
│   ├── utils/         # Utility classes
│   └── viewmodel/     # ViewModels
└── res/               # Resources
    ├── layout/        # XML layouts
    ├── values/        # Strings, colors, themes
    └── drawable/      # Images and icons
```

## Key Features Implementation

### Authentication
- JWT token management
- Secure credential storage
- Biometric authentication support
- Session management

### Event Management
- Create and edit events
- Upload event images
- Set event location
- Manage participants

### Offline Support
- Local database caching
- Background sync
- Offline event creation
- Image caching

## Development Guidelines

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable names
- Add KDoc comments for public APIs
- Keep functions focused and small

### Architecture
- Follow MVVM pattern
- Use Repository pattern for data operations
- Implement Clean Architecture principles
- Use Dependency Injection

## Testing

### Unit Tests
```bash
./gradlew test
```

### UI Tests
```bash
./gradlew connectedAndroidTest
```

## Building Release APK

### 1. Generate Keystore
```bash
keytool -genkey -v -keystore eventplanner.keystore -alias eventplanner -keyalg RSA -keysize 2048 -validity 10000
```

### 2. Configure Signing
Add to `app/build.gradle`:
```gradle
android {
    signingConfigs {
        release {
            storeFile file("eventplanner.keystore")
            storePassword "your-store-password"
            keyAlias "eventplanner"
            keyPassword "your-key-password"
        }
    }
}
```

### 3. Build Release APK
```bash
./gradlew assembleRelease
```

## Troubleshooting

### Common Issues
1. Build Failures
   - Clean project and rebuild
   - Update Gradle version
   - Check SDK installation

2. API Connection
   - Verify network permissions
   - Check API endpoint configuration
   - Validate SSL certificates

3. Image Loading
   - Check storage permissions
   - Verify image cache configuration
   - Monitor memory usage

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License
[Your License Here]

## Support
For support, email [Your Support Email]

## Authors
[Your Name/Team]

## Acknowledgments
- Android Team
- Kotlin Team
- All contributors 