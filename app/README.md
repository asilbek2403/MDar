# MyApplication - Android/Kotlin Version

A native Android application written in Kotlin that displays a map with markers and allows the user to:
- See their current location
- Tap a button to center the map on their location
- Tap on markers to see the name of the place
- Calculate distance from current location to a marker (example: to Universitet)

## Features
- Uses Google Maps SDK (requires API key)
- Requests location permission and shows the user's current location
- Three markers: Universitet, Talabaning uyi, and Bahoriyot park
- When a marker is clicked, a toast shows the place name
- Button to center map on user's current location
- Button to calculate distance to the first marker (Universitet) as an example

## Prerequisites
- Android Studio (Arctic Fox or newer recommended)
- Google Maps API key (get one from [Google Cloud Console](https://console.cloud.google.com/))
- Android SDK installed

## Setup

1. **Get a Google Maps API key:**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select existing
   - Enable "Maps SDK for Android"
   - Create an API key under "Credentials"
   - Restrict the key to your Android app's package name and SHA-1 certificate for security

2. **Add your API key:**
   - Open `src/main/res/values/strings.xml`
   - Replace `YOUR_API_KEY_HERE` with your actual Google Maps API key
   ```xml
   <string name="google_maps_key">YOUR_ACTUAL_API_KEY_HERE</string>
   ```

3. **Open the project in Android Studio:**
   - Launch Android Studio
   - Choose "Open an Existing Project"
   - Navigate to `C:\Users\Docker\Desktop\lessonMohirdev\MyApplication\app`
   - Select the `build.gradle` file and click OK

4. **Sync and run:**
   - Click "Sync Now" if prompted
   - Connect an Android device or start an emulator
   - Click the "Run" button (or press Shift+F10)

## How It Works

### Permissions
The app requests `ACCESS_FINE_LOCATION` permission at runtime. If granted, it enables the My Location layer on the map.

### Map Initialization
- The map is initialized in `MainActivity.kt` using `SupportMapFragment`
- Three markers are added for Universitet, Talabaning uyi, and Bahoriyot park
- The camera is initially positioned over Uzbekistan

### User Location
- When location permission is granted, the app starts listening for location updates
- A blue marker shows the user's current location and moves as the user moves
- The "Mening joylashuvim" button animates the map to the user's current location

### Distance Calculation
- The "Masofani hisoblash" button calculates the distance (in kilometers) between the user's current location and the first marker (Universitet)
- Uses the Haversine formula for accurate distance calculation on Earth's surface
- Displays result in a toast

### Marker Interaction
- Tapping on any marker shows a toast with the marker's title (place name)

## Files
- `MainActivity.kt` - Main application logic
- `activity_main.xml` - Layout with map fragment and two buttons
- `AndroidManifest.xml` - App permissions and metadata
- `build.gradle` - Project dependencies
- `strings.xml` - App name and Google Maps API key

## Notes
- This implementation uses Google Maps SDK, which requires an API key
- For OpenStreetMap without API keys, a different approach (like using OSMdroid or a custom tile provider) would be needed
- The distance calculation is approximate but accurate for most purposes
- Make sure to test on a real device or emulator with Google Play Services installed

## Troubleshooting
- **Map not showing:** Check your API key and ensure Maps SDK for Android is enabled in Google Cloud Console
- **Location not working:** Ensure you granted location permission and that location is enabled on your device/emulator
- **API key restrictions:** If you get API key errors, check your key restrictions in Google Cloud Console

## License
This project is for educational purposes.
