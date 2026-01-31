package alt.archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import com.aliucord.api.SettingsAPI
import com.aliucord.widgets.BottomSheet
import android.view.View
import android.os.Bundle
import com.discord.views.CheckedSetting
import com.aliucord.Utils
import com.aliucord.utils.ViewUtils.addTo
import com.aliucord.SettingsUtilsJSON
import org.json.*
import com.aliucord.utils.GsonUtils
import java.lang.reflect.*

import android.content.Context
import com.discord.stores.StoreStream
import android.content.SharedPreferences
import com.aliucord.patcher.*

import com.discord.utilities.persister.Persister
import java.lang.ref.WeakReference

class SettingsBottom(val settings: SettingsAPI): BottomSheet(){};

fun JSONObject.toMap(): Map<String, Any>{
	return this.keys().asSequence()
		.associateWith{this.get(it)}
	;
};
fun JSONArray.toList(): List<Any>{
	return this.iterator().asSequence().toList();
};

@AliucordPlugin(requiresRestart = true)
@SuppressLint("SetTextI18n")
class SettingsBackup: Plugin(){

	init{
		settingsTab = SettingsTab(object: BottomSheet(){
			override fun onViewCreated(view: View, bundle: Bundle?){
				super.onViewCreated(view, bundle);
				val settingsContext = requireContext();
				Utils.createCheckedSetting(
					settingsContext,
					CheckedSetting.ViewType.SWITCH,
					"Backup private settings",
					"Includes discord token, username, e-mail etc.",
				).addTo(linearLayout){
					isChecked = settings.getBool("expose_private_settings", false);
					setOnCheckedListener{state: Boolean ->
						settings.setBool("expose_private_settings", state);
					};
				};
				Utils.createCheckedSetting(
					settingsContext,
					CheckedSetting.ViewType.SWITCH,
					"Update backup",
					"Whether to continously update the backup with new changes",
				).addTo(linearLayout){
					isChecked = settings.getBool("write_backup", true);
					setOnCheckedListener{state ->
						settings.setBool("write_backup", state);
					};
				};
			};
		}::class.java);
	};

	val backup = SettingsUtilsJSON("Discord");
	fun json(string: String): Any{
		if(string == "") return string;
		return when(string[0]){
			'{' -> JSONObject(string).toMap();
			'[' -> JSONArray(string).toList();
			//'"' -> string.drop(1).dropLast(1);
			't' -> true;
			'f' -> false;
			'n' -> JSONObject.NULL;
			in '0'..'9' -> string.toDouble();
			else -> string
		};
	};

	val privateKeys = arrayOf(
		"LOG_CACHE_KEY_USER_LOGIN",
		"STORE_AUTHED_TOKEN",
		"STORE_AUTH_STATE",
		"LOG_CACHE_KEY_USER_ID",
		"LOG_CACHE_KEY_USER_NAME"
	);
	fun handlePrivate(prefs: Map<String, *>, exposePrivate: Boolean): Map<String, *>{
		if(exposePrivate){
			return prefs;
		}else{
			return prefs.filterKeys{it !in privateKeys};
		};
	};

	val storeKeys = arrayOf(
		"NOTIFICATION_CLIENT_SETTINGS_V3",
		"STORE_SHOW_HIDE_MUTED_CHANNELS_V2",
		"CACHE_KEY_SELECTED_CHANNEL_IDS",
		"STORE_NOTIFICATIONS_SETTINGS_V2",
		"USER_EXPERIMENTS_CACHE_KEY",
		"GUILD_EXPERIMENTS_CACHE_KEY ",
		"EXPERIMENT_OVERRIDES_CACHE_KEY",
		"CACHE_KEY_ACCESSIBILITY_REDUCED_MOTION_ENABLED",
		"RESTRICTED_GUILD_IDS",//no idea what this is
		"STORE_SETTINGS_FOLDERS_V1",
		"STORE_SETTINGS_ALLOW_ANIMATED_EMOJIS ",
		"CACHE_KEY_STICKER_ANIMATION_SETTINGS_V1",
		"STORE_SETTINGS_ALLOW_GAME_STATUS",
		"CACHE_KEY_STICKER_SUGGESTIONS",
		"STORE_SETTINGS_ALLOW_ACCESSIBILITY_DETECTION ",
		"STORE_SETTINGS_AUTO_PLAY_GIFS ",
		"SEARCH_HISTORY_V2",
		"PREFERRED_VIDEO_INPUT_DEVICE_GUID",
		"CACHE_KEY_NATIVE_ENGINE_EVER_INITIALIZED",//no idea what's this either
		"NOTICES_SHOWN_V2",
		"CACHE_KEY_OPEN_FOLDERS",
		//"STORE_FAVORITES",
		//"EMOJI_HISTORY_V4",
		//"STICKER_HISTORY_V1",
		"CACHE_KEY_SELECTED_EXPRESSION_TRAY_TAB",
		"CACHE_KEY_APPLICATION_COMMANDS",
		"CACHE_KEY_PHONE_COUNTRY_CODE_V2",
		"CONTACT_SYNC_DISMISS_STATE",
		"hub_verification_clicked_key",
		"guild_scheduled_events_header_dismissed",
		"hub_name_prompt"
	);
	fun optPersisters(): MutableMap<String, Any>?{
		val obj = backup.getJSONObject("persisters", null);
		return obj?.toMap();
	};
	val fPersisterValue = Persister::class.java
		.getDeclaredField("value")
		.apply{isAccessible = true}
	;
	fun <T>serializePersister(p: Persister<T>): Pair<String, T>{ 
		return Pair(p.getKey(), fPersisterValue.get(p) as T);
	};
	fun <T>deserializePersisterValue(valueString: String, persister: Persister<T>): T{
		val currentValue = fPersisterValue.get(persister);
		val type = currentValue::class.java as Type
		val value = GsonUtils.fromJson(valueString, type) as T;
		return value;
	};

	override fun start(pluginContext: Context){

		//auth settings
		val storeAuth = StoreStream.getAuthentication();
		val editor: SharedPreferences.Editor = storeAuth.prefs.edit();

		//track number types
		var types = backup.getJSONObject("types", null);
		if(types == null){
			types = JSONObject();
			val currentAuth = storeAuth.prefs.all;
			for((key, value) in currentAuth){
				when(value){
					is Int -> types.put(key, "Int");
					is Long -> types.put(key, "Long");
					is Float -> types.put(key, "Float");
				};
			};
			backup.setJSONObject("types", types);
		};

		//import from backup
		val auth = backup.getObject("auth", mutableMapOf<String, Any>());
		val exposePrivate = settings.getBool("expose_private_settings", false);
		if(!auth.isEmpty()){
			for((key, value) in auth){
				when(value){
					is String -> editor.putString(key, value);
					is Int -> editor.putInt(key, value);
					is Boolean -> editor.putBoolean(key, value);
					is Float -> editor.putFloat(key, value);
					is Long -> editor.putLong(key, value);
					is Set<*> -> if(value.all{it is String}) editor.putStringSet(key, value as Set<String>);
					//conversions
					is Double -> {
						when(types.optString(key)){
							"Int" -> editor.putInt(key, value.toInt());
							"Long" -> editor.putLong(key, value.toLong());
							"Float" -> editor.putFloat(key, value.toFloat());
						};
					};
					is ArrayList<*> -> if(value.all{it is String}) editor.putStringSet(key, value.toSet() as Set<String>);
					else -> logger.error(Error("rejected key: $key\nof class: ${value::class}"));
				};
			};
			var success = false;
			while(!success){
				success = editor.commit();
			};
		}else{
			val writeBackup = settings.getBool("write_backup", true);
			//export current settings to backup
			if(writeBackup){
				val currentAuth = handlePrivate(storeAuth.prefs.all, exposePrivate);
				backup.setObject("auth", currentAuth);
			};
		};

		//export current settings to backup as they change
		patcher.patch(editor::class.java.getDeclaredMethod("apply"), PreHook{frame ->
			val writeBackup = settings.getBool("write_backup", true);
			if(writeBackup){
				val currentAuth = handlePrivate(storeAuth.prefs.all, exposePrivate);
				backup.setObject("auth", currentAuth);
			};
		});
		patcher.patch(editor::class.java.getDeclaredMethod("commit"), PreHook{frame ->
			val writeBackup = settings.getBool("write_backup", true);
			if(writeBackup){
				val currentAuth = handlePrivate(storeAuth.prefs.all, exposePrivate);
				backup.setObject("auth", currentAuth);
			};
		});

		//other stores and settings

		//import from backup
		val backupPersisters: MutableMap<String, Any>? = optPersisters();
		if(backupPersisters != null && !backupPersisters.isEmpty()){
			patcher.patch(Persister::class.java.getDeclaredMethod("get"), PreHook{frame ->
				val original = frame.thisObject as Persister<*>;
				val value = backupPersisters[original.getKey()];
				if(value != null){
					frame.result = deserializePersisterValue(value.toString(), original);
				};
			});
		}else{
			//export current settings to backup
			val currentPersisters = Persister.`access$getPreferences$cp`()//List<WeakReference<Persister<*>>>
				.mapNotNull{(it as WeakReference<Persister<*>>).get()}
				.filter{it.getKey() in storeKeys}
				.map{serializePersister(it) as Pair<String, Any>}
				.toMap() as Map<String, *>
			;
			backup.setObject("persisters", currentPersisters);
		};

		//export current settings to backup as they change
		patcher.patch(Persister::class.java.getDeclaredMethod("set", Any::class.java, Boolean::class.java), PreHook{frame ->
			val _this = frame.thisObject as Persister<*>;
			val key = _this.getKey();
			val valueString = GsonUtils.toJson(frame.args[0]);
			if(key in storeKeys && backupPersisters != null){
				backupPersisters[key] = json(valueString);
				backup.setObject("persisters", backupPersisters);
			};
		});

	};

	override fun stop(pluginContext: Context) = patcher.unpatchAll();

};