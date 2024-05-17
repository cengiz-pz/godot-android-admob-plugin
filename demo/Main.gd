#
# © 2024-present https://github.com/cengiz-pz
#

extends Node

@onready var admob: Admob = $Admob as Admob
@onready var banner_button: Button = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/BannerButton
@onready var interstitial_button: Button = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/InterstitialButton
@onready var rewarded_button: Button = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/RewardedButton
@onready var rewarded_interstitial_button: Button = $CanvasLayer/CenterContainer/VBoxContainer/VBoxContainer/RewardedInterstitialButton
@onready var _label: RichTextLabel = $CanvasLayer/CenterContainer/VBoxContainer/RichTextLabel as RichTextLabel

var _is_banner_loaded: bool = false
var _is_interstitial_loaded: bool = false
var _is_rewarded_video_loaded: bool = false
var _is_rewarded_interstitial_loaded: bool = false


func _ready() -> void:
	admob.initialize()


func _on_admob_initialization_completed(status_data: InitializationStatus) -> void:
	admob.load_banner_ad()
	admob.load_interstitial_ad()
	admob.load_rewarded_ad()
	admob.load_rewarded_interstitial_ad()


func _on_banner_button_pressed() -> void:
	print(" ------- Banner button PRESSED")
	if _is_banner_loaded:
		_is_banner_loaded = false
		banner_button.disabled = true
		admob.show_banner_ad()
	else:
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


func _on_admob_banner_ad_loaded(ad_id: String) -> void:
	_is_banner_loaded = true
	banner_button.disabled = false
	_print_to_screen("banner loaded: %s" % ad_id)


func _on_admob_banner_ad_failed_to_load(ad_id: String, error_data: LoadAdError) -> void:
	_print_to_screen("banner failed to load. error: %d, message: %s" %
				[error_data.get_code(), error_data.get_message()], true)


func _on_admob_interstitial_ad_loaded(ad_id: String) -> void:
	_is_interstitial_loaded = true
	interstitial_button.disabled = false
	_print_to_screen("interstitial loaded: %s" % ad_id)


func _on_admob_interstitial_ad_failed_to_load(ad_id: String, error_data: LoadAdError) -> void:
	_print_to_screen("interstitial failed to load. error: %d, message: %s" %
				[error_data.get_code(), error_data.get_message()], true)


func _on_admob_rewarded_ad_loaded(ad_id: String) -> void:
	_is_rewarded_video_loaded = true
	rewarded_button.disabled = false
	_print_to_screen("rewarded video loaded: %s" % ad_id)


func _on_admob_rewarded_ad_failed_to_load(ad_id: String, error_data: LoadAdError) -> void:
	_print_to_screen("rewarded video failed to load. error: %d, message: %s" %
				[error_data.get_code(), error_data.get_message()], true)


func _on_admob_rewarded_ad_user_earned_reward(ad_id: String, reward_data: RewardItem) -> void:
	_print_to_screen("user rewarded for rewarded ad '%s' with %d %s" %
				[ad_id, reward_data.get_amount(), reward_data.get_type()])


func _on_admob_rewarded_interstitial_ad_loaded(ad_id: String) -> void:
	_is_rewarded_interstitial_loaded = true
	rewarded_interstitial_button.disabled = false
	_print_to_screen("rewarded interstitial loaded: %s" % ad_id)


func _on_admob_rewarded_interstitial_ad_failed_to_load(ad_id: String, error_data: LoadAdError) -> void:
	_print_to_screen("rewarded interstitial failed to load. error: %d, message: %s" %
				[error_data.get_code(), error_data.get_message()], true)


func _on_admob_rewarded_interstitial_ad_user_earned_reward(ad_id: String, reward_data: RewardItem) -> void:
	_print_to_screen("user rewarded for rewarded interstitial ad '%s' with %d %s" %
				[ad_id, reward_data.get_amount(), reward_data.get_type()])


func _print_to_screen(a_message: String, a_is_error: bool = false) -> void:
	_label.add_text("%s\n\n" % a_message)
	if a_is_error:
		printerr(a_message)
	else:
		print(a_message)
