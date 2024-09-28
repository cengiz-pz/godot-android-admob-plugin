#
# © 2024-present https://github.com/cengiz-pz
#

@tool
extends EditorPlugin

const PLUGIN_NODE_TYPE_NAME = "Admob"
const PLUGIN_PARENT_NODE_TYPE = "Node"
const PLUGIN_NAME: String = "@pluginName@"
const PLUGIN_VERSION: String = "@pluginVersion@"
const PLUGIN_DEPENDENCIES: Array = [ @pluginDependencies@ ]

const APP_ID_META_TAG = """
<meta-data
		tools:replace="android:value"
		android:name="com.google.android.gms.ads.APPLICATION_ID"
		android:value="%s"/>
"""

var export_plugin: AndroidExportPlugin


func _enter_tree() -> void:
	add_custom_type(PLUGIN_NODE_TYPE_NAME, PLUGIN_PARENT_NODE_TYPE, preload("Admob.gd"), preload("icon.png"))
	export_plugin = AndroidExportPlugin.new()
	add_export_plugin(export_plugin)


func _exit_tree() -> void:
	remove_custom_type(PLUGIN_NODE_TYPE_NAME)
	remove_export_plugin(export_plugin)
	export_plugin = null


class AndroidExportPlugin extends EditorExportPlugin:
	var _plugin_name = PLUGIN_NAME


	func _supports_platform(platform: EditorExportPlatform) -> bool:
		if platform is EditorExportPlatformAndroid:
			return true
		return false


	func _get_android_libraries(platform: EditorExportPlatform, debug: bool) -> PackedStringArray:
		if debug:
			return PackedStringArray(["%s/bin/debug/%s-%s-debug.aar" % [_plugin_name, _plugin_name, PLUGIN_VERSION]])
		else:
			return PackedStringArray(["%s/bin/release/%s-%s-release.aar" % [_plugin_name, _plugin_name, PLUGIN_VERSION]])


	func _get_name() -> String:
		return _plugin_name


	func _get_android_dependencies(platform: EditorExportPlatform, debug: bool) -> PackedStringArray:
		return PackedStringArray(PLUGIN_DEPENDENCIES)


	func _get_android_manifest_application_element_contents(platform: EditorExportPlatform, debug: bool) -> String:
		var __admob_node: Admob = _get_admob_node(EditorInterface.get_edited_scene_root())
		if not __admob_node:
			var main_scene = load(ProjectSettings.get_setting("application/run/main_scene")).instantiate()
			__admob_node = _get_admob_node(main_scene)
			if not __admob_node:
				push_error("%s failed to find Admob node!" % PLUGIN_NAME)

		return APP_ID_META_TAG % (__admob_node.real_application_id if __admob_node.is_real else __admob_node.debug_application_id)


	func _get_admob_node(a_node: Node) -> Admob:
		var __result: Admob

		if a_node is Admob:
			__result = a_node
		elif a_node.get_child_count() > 0:
			for __child in a_node.get_children():
				var __child_result = _get_admob_node(__child)
				if __child_result is Admob:
					__result = __child_result
					break

		return __result
