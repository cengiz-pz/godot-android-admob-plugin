
---
# ![](admob/addon_template/icon.png?raw=true) Android Admob Plugin

Enables AdMob functionality on Godot apps that are exported to the Android platform and allows 
displaying of Admob ads.

_For iOS version, visit https://github.com/cengiz-pz/godot-ios-admob-plugin ._

This branch contains the latest version of the plugin, which contains breaking changes to the plugin
interface. The original version of the plugin can be found on the
[Release 1.0 branch](https://github.com/cengiz-pz/godot-android-admob-plugin/tree/release-1.0).

## ![](admob/addon_template/icon.png?raw=true) Prerequisites
Follow instructions on the following page to create a custom Android gradle build
- [Create custom Android gradle build](https://docs.godotengine.org/en/stable/tutorials/export/android_gradle_build.html)

Create an AdMob account at the following link:
- [Google AdMob](https://admob.google.com/)
- create an App in Admob console
- create ads for your App via the Admob console

## ![](admob/addon_template/icon.png?raw=true) Installation
There are 2 ways to install the `Admob` plugin into your project:
- Through the Godot Editor's AssetLib
- Manually by downloading archives from Github

### ![](admob/addon_template/icon.png?raw=true) Installing via AssetLib
Steps:
- search for and select the `Admob` plugin in Godot Editor
- click `Download` button
- on the installation dialog...
	- keep `Change Install Folder` setting pointing to your project's root directory
	- keep `Ignore asset root` checkbox checked
	- click `Install` button
- enable the plugin via the `Plugins` tab of `Project->Project Settings...` menu, in the Godot Editor

### ![](admob/addon_template/icon.png?raw=true) Installing manually
Steps:
- download release archive from Github
- unzip the release archive
- copy to your Godot project's root directory
- enable the plugin via the `Plugins` tab of `Project->Project Settings...` menu, in the Godot Editor

## ![](admob/addon_template/icon.png?raw=true) Usage
- Add `Admob` node to your main scene and populate the ID fields of the node
	- Debug IDs will only be used when your Godot app is run in debug mode
	- Real IDs will only be used when the `is_real` field of the node is set to `true`

- register listeners for one or more of the following signals of the `Admob` node:
	- `initialization_completed(status_data: InitializationStatus)`
	- `banner_ad_loaded(ad_id: String)`
	- `banner_ad_failed_to_load(ad_id: String, error_data: LoadAdError)`
	- `banner_ad_refreshed(ad_id: String)`
	- `banner_ad_clicked(ad_id: String)`
	- `banner_ad_impression(ad_id: String)`
	- `banner_ad_opened(ad_id: String)`
	- `banner_ad_closed(ad_id: String)`
	- `interstitial_ad_loaded(ad_id: String)`
	- `interstitial_ad_failed_to_load(ad_id: String, error_data: LoadAdError)`
	- `interstitial_ad_impression(ad_id: String)`
	- `interstitial_ad_clicked(ad_id: String)`
	- `interstitial_ad_showed_full_screen_content(ad_id: String)`
	- `interstitial_ad_failed_to_show_full_screen_content(ad_id: String, error_data: AdError)`
	- `interstitial_ad_dismissed_full_screen_content(ad_id: String)`
	- `rewarded_ad_loaded(ad_id: String)`
	- `rewarded_ad_failed_to_load(ad_id: String, error_data: LoadAdError)`
	- `rewarded_ad_impression(ad_id: String)`
	- `rewarded_ad_clicked(ad_id: String)`
	- `rewarded_ad_showed_full_screen_content(ad_id: String)`
	- `rewarded_ad_failed_to_show_full_screen_content(ad_id: String, error_data: AdError)`
	- `rewarded_ad_dismissed_full_screen_content(ad_id: String)`
	- `rewarded_ad_user_earned_reward(ad_id: String, reward_data: RewardItem)`
	- `rewarded_interstitial_ad_loaded(ad_id: String)`
	- `rewarded_interstitial_ad_failed_to_load(ad_id: String, error_data: LoadAdError)`
	- `rewarded_interstitial_ad_impression(ad_id: String)`
	- `rewarded_interstitial_ad_clicked(ad_id: String)`
	- `rewarded_interstitial_ad_showed_full_screen_content(ad_id: String)`
	- `rewarded_interstitial_ad_failed_to_show_full_screen_content(ad_id: String, error_data: AdError)`
	- `rewarded_interstitial_ad_dismissed_full_screen_content(ad_id: String)`
	- `rewarded_interstitial_ad_user_earned_reward(ad_id: String, reward_data: RewardItem)`
	- `consent_form_loaded`
	- `consent_form_dismissed(error_data: FormError)`
	- `consent_form_failed_to_load(error_data: FormError)`
	- `consent_info_updated`
	- `consent_info_update_failed(error_data: FormError)`
- initialize the plugin
	- call the `initialize()` method of the `Admob` node
	- wait for the `initialization_completed` signal
- use one or more of the following `load_*()` methods to load ads from the `Admob` node:
	- `load_banner_ad(ad_request: LoadAdRequest)`
	- `load_interstitia_adl(ad_request: LoadAdRequest)`
	- `load_rewarded_ad(ad_request: LoadAdRequest)`
	- `load_rewarded_interstitial_ad(ad_request: LoadAdRequest)`
- the `Admob` node will emit the following signals once ads have been loaded or failed to load:
	- `banner_ad_loaded(ad_id: String)`
	- `banner_ad_failed_to_load(ad_id: String, error_data: LoadAdError)`
	- `interstitial_ad_loaded(ad_id: String)`
	- `interstitial_ad_failed_to_load(ad_id: String, error_data: LoadAdError)`
	- `rewarded_ad_loaded(ad_id: String)`
	- `rewarded_ad_failed_to_load(ad_id: String, error_data: LoadAdError)`
	- `rewarded_interstitial_ad_loaded(ad_id: String)`
	- `rewarded_interstitial_ad_failed_to_load(ad_id: String, error_data: LoadAdError)`
- once ads have been loaded, call corresponding `show_*()` method from the `Admob` node with the `ad_id` received:
	- `show_banner_ad(ad_id: String)`
	- `show_interstitial_ad(ad_id: String)`
	- `show_rewarded_ad(ad_id: String)`
	- `show_rewarded_interstitial_ad(ad_id: String)`

## ![](admob/addon_template/icon.png?raw=true) Android Export
- Make sure that the scene that contains the Admob node is selected in the Godot Editor when building and exporting for Android
	- Close other scenes to make sure
	- _Admob node will be searched in the scene that is currently open in the Godot Editor_

## ![](admob/addon_template/icon.png?raw=true) Troubleshooting
`adb logcat` is one of the best tools for troubleshooting unexpected behavior
- use `$> adb logcat | grep 'godot'` on Linux
	- `adb logcat *:W` to see warnings and errors
	- `adb logcat *:E` to see only errors
	- `adb logcat | grep 'godot|somethingElse'` to filter using more than one string at the same time
- use `#> adb.exe logcat | select-string "godot"` on powershell (Windows)

Also check out:
https://docs.godotengine.org/en/stable/tutorials/platform/android/android_plugin.html#troubleshooting
<br/><br/><br/>

---
# ![](admob/addon_template/icon.png?raw=true) Credits
Based on [Shin-NiL](https://github.com/Shin-NiL)'s [Godot Admob Plugin](https://github.com/Shin-NiL/Godot-Android-Admob-Plugin)

Developed by [Cengiz](https://github.com/cengiz-pz)

Original repository: [Godot Android Admob Plugin](https://github.com/cengiz-pz/godot-android-admob-plugin)
