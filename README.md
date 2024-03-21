# ![](admob/addon_template/icon.png?raw=true) Android Admob Plugin

Enables AdMob functionality on Godot apps that are exported to the Android platform and allows 
displaying of Admob ads.

_Based on Shin-NiL's [Godot AdMob plugin](https://github.com/Shin-NiL/Godot-Android-Admob-Plugin)_

## ![](admob/addon_template/icon.png?raw=true) Prerequisites
Follow instructions on the following page to create a custom Android gradle build
- [Create custom Android gradle build](https://docs.godotengine.org/en/stable/tutorials/export/android_gradle_build.html)

- Create an `addons` directory in your project's root level.

Create an AdMob account at the following link:
- [Google AdMob](https://admob.google.com/)
- create an App in Admob console
- attach ad IDs to your App via the Admob console

## ![](admob/addon_template/icon.png?raw=true) Installation
There are 2 ways to install the `Admob` plugin into your project:
- Through the Godot Editor's AssetLib
- Manually by downloading archives from Github

### ![](admob/addon_template/icon.png?raw=true) Installing via AssetLib
Steps:
- search for and select the `Admob` plugin in Godot Editor
- click `Download` button
- on the installation dialog...
  - click `Change Install Folder` button and select your project's `addons` directory
  - uncheck `Ignore asset root` checkbox
  - click `Install` button
- enable the plugin via the `Plugins` tab of `Project->Project Settings...` menu, in the Godot Editor

### ![](admob/addon_template/icon.png?raw=true) Installing manually
Steps:
- download release archive from Github
- unzip the release archive
- copy to your Godot project's `addons` directory
- enable the plugin via the `Plugins` tab of `Project->Project Settings...` menu, in the Godot Editor

## ![](admob/addon_template/icon.png?raw=true) Usage
- Add `Admob` node to your main scene and populate the ID fields of the node
  - Debug IDs will only be used when your Godot app is run in debug mode
  - Real IDs will only be used when the `is_real` field of the node is set to `true`
- make sure that the scene that contains the Admob node is selected in the Godot Editor when building and exporting for Android
  - Close other scenes to make sure
  - _Admob node will be searched in the scene that is currently open in the Godot Editor_
- register listeners for one or more of the following signals of the `Admob` node:
    - `banner_loaded`
    - `banner_failed_to_load`
    - `interstitial_loaded`
    - `interstitial_opened`
    - `interstitial_closed`
    - `interstitial_clicked`
    - `interstitial_impression`
    - `interstitial_failed_to_load`
    - `interstitial_failed_to_show`
    - `rewarded_video_opened`
    - `rewarded_video_loaded`
    - `rewarded_video_closed`
    - `rewarded_video_failed_to_load`
    - `rewarded_video_failed_to_show`
    - `rewarded_interstitial_opened`
    - `rewarded_interstitial_loaded`
    - `rewarded_interstitial_closed`
    - `rewarded_interstitial_failed_to_load`
    - `rewarded_interstitial_failed_to_show`
    - `rewarded`
    - `rewarded_clicked`
    - `rewarded_impression`
- use one or more of the following `load_*()` methods to load ads from the `Admob` node:
    - `load_banner()`
    - `load_interstitial()`
    - `load_rewarded_video()`
    - `load_rewarded_interstitial()`
- once ads have been loaded, call corresponding `show_*()` method from the `Admob` node:
    - `show_banner()`
    - `show_interstitial()`
    - `show_rewarded_video()`
    - `show_rewarded_interstitial()`

## ![](admob/addon_template/icon.png?raw=true) Troubleshooting
`adb logcat` is one of the best tools for troubleshooting unexpected behavior
- use `$> adb logcat | grep 'godot'` on Linux
    - `adb logcat *:W` to see warnings and errors
    - `adb logcat *:E` to see only errors
    - `adb logcat | grep 'godot|somethingElse'` to filter using more than one string at the same time
- use `#> adb.exe logcat | select-string "godot"` on powershell (Windows)

Also check out:
https://docs.godotengine.org/en/stable/tutorials/platform/android/android_plugin.html#troubleshooting
