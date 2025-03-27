#
# © 2024-present https://github.com/cengiz-pz
#

extends Node

@onready var admob: Admob = $Admob as Admob
@onready var show_banner_button: Button = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/TabContainer/Banner/ButtonsHBoxContainer/ShowBannerButton
@onready var hide_banner_button: Button = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/TabContainer/Banner/ButtonsHBoxContainer/HideBannerButton
@onready var reload_banner_button: Button = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/TabContainer/Banner/ButtonsHBoxContainer/ReloadBannerButton
@onready var banner_position_option_button: OptionButton = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/TabContainer/Banner/PositionHBoxContainer/OptionButton
@onready var banner_size_option_button: OptionButton = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/TabContainer/Banner/SizeHBoxContainer/OptionButton
@onready var interstitial_button: Button = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/TabContainer/Other/InterstitialButton
@onready var rewarded_button: Button = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/TabContainer/Other/RewardedButton
@onready var rewarded_interstitial_button: Button = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/TabContainer/Other/RewardedInterstitialButton
@onready var _label: RichTextLabel = $CanvasLayer/CenterContainer/VBoxContainer/RichTextLabel as RichTextLabel
@onready var _geography_option_button: OptionButton = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/GeographyHBoxContainer/OptionButton

var _is_banner_loaded: bool = false
var _is_interstitial_loaded: bool = false
var _is_rewarded_video_loaded: bool = false
var _is_rewarded_interstitial_loaded: bool = false


func _ready() -> void:
	admob.initialize()

	var __index: int = 0
	for __item: String in LoadAdRequest.AdPosition.keys():
		banner_position_option_button.add_item(__item)
		if __item.casecmp_to(LoadAdRequest.AdPosition.keys()[admob.banner_position]) == 0:
			banner_position_option_button.select(__index)
		__index += 1

	__index = 0
	for __item in LoadAdRequest.AdSize.keys():
		banner_size_option_button.add_item(__item)
		if __item.casecmp_to(LoadAdRequest.AdSize.keys()[admob.banner_size]) == 0:
			banner_size_option_button.select(__index)
		__index += 1


func _on_admob_initialization_completed(_status_data: InitializationStatus) -> void:
	_process_consent_status(admob.get_consent_status())
	admob.load_banner_ad()
	admob.load_interstitial_ad()
	admob.load_rewarded_ad()
	admob.load_rewarded_interstitial_ad()


func _on_size_button_pressed() -> void:
	print(" ------- Get banner size button PRESSED")
	if _is_banner_loaded:
		_print_to_screen("Banner size: " + str(admob.get_banner_dimension()))


func _on_pixel_size_button_pressed() -> void:
	print(" ------- Get banner pixel size button PRESSED")
	if _is_banner_loaded:
		_print_to_screen("Banner size in pixels: " + str(admob.get_banner_dimension_in_pixels()))


func _on_show_banner_button_pressed() -> void:
	print(" ------- Show banner button PRESSED")
	if _is_banner_loaded:
		show_banner_button.disabled = true
		hide_banner_button.disabled = false
		admob.show_banner_ad()


func _on_hide_banner_button_pressed() -> void:
	print(" ------- Hide banner button PRESSED")
	if _is_banner_loaded:
		show_banner_button.disabled = false
		hide_banner_button.disabled = true
		admob.hide_banner_ad()


func _on_reload_banner_button_pressed() -> void:
	print(" ------- Reload banner button PRESSED")
	if _is_banner_loaded:
		_is_banner_loaded = false
		show_banner_button.disabled = true
		reload_banner_button.disabled = true
		admob.remove_banner_ad(admob._active_banner_ads[0])
		admob.banner_position = LoadAdRequest.AdPosition[banner_position_option_button.get_item_text(banner_position_option_button.selected)]
		admob.banner_size = LoadAdRequest.AdSize[banner_size_option_button.get_item_text(banner_size_option_button.selected)]
		admob.load_banner_ad()


func _on_interstitial_button_pressed() -> void:
	print(" ------- Interstitial button PRESSED")
	if _is_interstitial_loaded:
		_is_interstitial_loaded = false
		interstitial_button.disabled = true
		admob.show_interstitial_ad()
	else:
		admob.load_interstitial_ad()


func _on_rewarded_video_button_pressed() -> void:
	print(" ------- Rewarded button PRESSED")
	if _is_rewarded_video_loaded:
		_is_rewarded_video_loaded = false
		rewarded_button.disabled = true
		admob.show_rewarded_ad()
	else:
		admob.load_rewarded_ad()


func _on_rewarded_interstitial_button_pressed() -> void:
	print(" ------- Rewarded interstitial button PRESSED")
	if _is_rewarded_interstitial_loaded:
		_is_rewarded_interstitial_loaded = false
		rewarded_interstitial_button.disabled = true
		admob.show_rewarded_interstitial_ad()
	else:
		admob.load_rewarded_interstitial_ad()


func _on_reset_consent_button_pressed() -> void:
	admob.reset_consent_info()


func _on_admob_banner_ad_loaded(ad_id: String) -> void:
	_is_banner_loaded = true
	show_banner_button.disabled = false
	reload_banner_button.disabled = false
	_print_to_screen("banner loaded: %s" % ad_id)


func _on_admob_banner_ad_refreshed(ad_id: String) -> void:
	_print_to_screen("banner refreshed: %s" % ad_id)


func _on_admob_banner_ad_failed_to_load(ad_id: String, error_data: LoadAdError) -> void:
	_print_to_screen("banner %s failed to load. error: %d, message: %s" %
				[ad_id, error_data.get_code(), error_data.get_message()], true)


func _on_admob_interstitial_ad_loaded(ad_id: String) -> void:
	_is_interstitial_loaded = true
	interstitial_button.disabled = false
	_print_to_screen("interstitial loaded: %s" % ad_id)


func _on_admob_interstitial_ad_failed_to_load(ad_id: String, error_data: LoadAdError) -> void:
	_print_to_screen("interstitial %s failed to load. error: %d, message: %s" %
				[ad_id, error_data.get_code(), error_data.get_message()], true)


func _on_admob_interstitial_ad_refreshed(ad_id: String) -> void:
	_print_to_screen("interstitial refreshed: %s" % ad_id)


func _on_admob_interstitial_ad_dismissed_full_screen_content(ad_id: String) -> void:
	_print_to_screen("interstitial closed: %s" % ad_id)


func _on_admob_rewarded_ad_loaded(ad_id: String) -> void:
	_is_rewarded_video_loaded = true
	rewarded_button.disabled = false
	_print_to_screen("rewarded video loaded: %s" % ad_id)


func _on_admob_rewarded_ad_failed_to_load(ad_id: String, error_data: LoadAdError) -> void:
	_print_to_screen("rewarded video %s failed to load. error: %d, message: %s" %
				[ad_id, error_data.get_code(), error_data.get_message()], true)


func _on_admob_rewarded_ad_user_earned_reward(ad_id: String, reward_data: RewardItem) -> void:
	_print_to_screen("user rewarded for rewarded ad '%s' with %d %s" %
				[ad_id, reward_data.get_amount(), reward_data.get_type()])


func _on_admob_rewarded_interstitial_ad_loaded(ad_id: String) -> void:
	_is_rewarded_interstitial_loaded = true
	rewarded_interstitial_button.disabled = false
	_print_to_screen("rewarded interstitial loaded: %s" % ad_id)


func _on_admob_rewarded_interstitial_ad_failed_to_load(ad_id: String, error_data: LoadAdError) -> void:
	_print_to_screen("rewarded interstitial %s failed to load. error: %d, message: %s" %
				[ad_id, error_data.get_code(), error_data.get_message()], true)


func _on_admob_rewarded_interstitial_ad_user_earned_reward(ad_id: String, reward_data: RewardItem) -> void:
	_print_to_screen("user rewarded for rewarded interstitial ad '%s' with %d %s" %
				[ad_id, reward_data.get_amount(), reward_data.get_type()])


func _on_admob_consent_info_updated() -> void:
	_print_to_screen("consent info updated")
	_process_consent_status(admob.get_consent_status())


func _on_admob_consent_info_update_failed(a_error_data: FormError) -> void:
	_print_to_screen("consent info failed to update: %s" % a_error_data.get_message())


func _process_consent_status(a_consent_status: int) -> void:
	_print_to_screen("_process_consent_status(): consent status = %d" % a_consent_status)
	match a_consent_status:
		ConsentInformation.ConsentStatus.UNKNOWN:
			_print_to_screen("consent status is unknown")
			admob.update_consent_info(ConsentRequestParameters.new()
				.set_debug_geography(ConsentRequestParameters.DebugGeography.values()[_geography_option_button.selected])
				.add_test_device_hashed_id(OS.get_unique_id()))
		ConsentInformation.ConsentStatus.NOT_REQUIRED:
			_print_to_screen("consent is not required")
		ConsentInformation.ConsentStatus.REQUIRED:
			_print_to_screen("consent is required")
			admob.load_consent_form()
		ConsentInformation.ConsentStatus.OBTAINED:
			_print_to_screen("consent has already been obtained")


func _on_admob_consent_form_loaded() -> void:
	_print_to_screen("consent form has been loaded")
	admob.show_consent_form()


func _on_admob_consent_form_failed_to_load(a_error_data: FormError) -> void:
	_print_to_screen("consent form failed to load %s" % a_error_data.get_message())


func _on_admob_consent_form_dismissed(a_error_data: FormError) -> void:
	_print_to_screen("consent form has been dismissed %s" % a_error_data.get_message())


func _on_update_consent_info_button_pressed() -> void:
	print("selected consent geography: %d" % _geography_option_button.selected)
	admob.update_consent_info(ConsentRequestParameters.new()
		.set_debug_geography(ConsentRequestParameters.DebugGeography.values()[_geography_option_button.selected])
		.add_test_device_hashed_id(OS.get_unique_id()))


func _print_to_screen(a_message: String, a_is_error: bool = false) -> void:
	_label.add_text("%s\n\n" % a_message)
	if a_is_error:
		printerr(a_message)
	else:
		print(a_message)


func _on_main_scene_button_pressed() -> void:
	get_tree().change_scene_to_file("res://Main.tscn")
