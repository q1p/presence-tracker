# Presence Tracker

Device presence tracking application aimed to detect if device is inside of specific geofences area and/or connected to specific Wi-Fi network.

## Prerequisites

This application relies on Geolocation services so it does need corresponding permissions and enabled GPS module on device.

Tracking is intended to be performed only while application is running (for simplicity).

Please note that responsiveness of Geofences tracking is about couple of minutes in average. [Details](https://developer.android.com/about/versions/oreo/background-location-limits#apis)

## Build

To build and install this application connect a device or emulator, then run the following command:

```shell
./gradlew installDebug
```

Or build an **.apk** file and then install it on device manually:

```shell
./gradlew assembleDebug
```
## Dependencies

* [AAC ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* [AAC LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [Google Play Services](https://developers.google.com/android/guides/setup) - Maps, Location
* [Google Maps Android API utility library](https://github.com/googlemaps/android-maps-utils)
* [EventBus](https://github.com/greenrobot/EventBus)

## License


    Copyright 2018 Victor Kosenko

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

