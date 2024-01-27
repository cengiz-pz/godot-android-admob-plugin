extends Node

@onready var admob: Admob = $Admob as Admob
@onready var _label: RichTextLabel = $CanvasLayer/CenterContainer/VBoxContainer/RichTextLabel as RichTextLabel

var _is_banner_loaded: bool = false
var _is_interstitial_loaded: bool = false
var _is_rewarded_video_loaded: bool = false
var _is_rewarded_interstitial_loaded: bool = false


func _ready() -> void:
	admob.load_banner()
	admob.load_interstitial()
	admob.load_rewarded_video()
	admob.load_rewarded_interstitial()


func _on_banner_button_pressed() -> void:
	if _is_banner_loaded:
		_is_banner_loaded = false
		admob.show_banner()
	else:
		admob.load_banner()


func _on_interstitial_button_pressed() -> void:
	if _is_interstitial_loaded:
		_is_interstitial_loaded = false
		admob.show_interstitial()
	else:
		admob.load_interstitial()


func _on_rewarded_interstitial_button_pressed() -> void:
	if _is_rewarded_interstitial_loaded:
		_is_rewarded_interstitial_loaded = false
		admob.show_rewarded_interstitial()
	else:
		admob.load_rewarded_interstitial()


func _on_rewarded_video_button_pressed() -> void:
	if _is_rewarded_video_loaded:
		_is_rewarded_video_loaded = false
		admob.show_rewarded_video()
	else:
		admob.load_rewarded_video()


func _on_admob_banner_loaded() -> void:
	_is_banner_loaded = true
	_print_to_screen("banner loaded")


func _on_admob_banner_failed_to_load(error_code: Variant) -> void:
	_print_to_screen("banner failed to load. error: %s" % str(error_code), true)


func _on_admob_interstitial_loaded() -> void:
	_is_interstitial_loaded = true
	_print_to_screen("interstitial loaded")


func _on_admob_interstitial_failed_to_load(error_code: Variant) -> void:
	_print_to_screen("interstitial failed to load. error: %s" % str(error_code), true)


func _on_admob_rewarded_interstitial_loaded() -> void:
	_is_rewarded_interstitial_loaded = true
	_print_to_screen("rewarded interstitial loaded")


func _on_admob_rewarded_interstitial_failed_to_load(error_code: Variant) -> void:
	_print_to_screen("rewarded interstitial failed to load. error: %s" % str(error_code), true)


func _on_admob_rewarded_video_loaded() -> void:
	_is_rewarded_video_loaded = true
	_print_to_screen("rewarded video loaded")


func _on_admob_rewarded_video_failed_to_load(error_code: Variant) -> void:
	_print_to_screen("rewarded video failed to load. error: %s" % str(error_code), true)


func _print_to_screen(a_message: String, a_is_error: bool = false) -> void:
	_label.add_text("%s\n\n" % a_message)
	if a_is_error:
		printerr(a_message)
	else:
		print(a_message)
