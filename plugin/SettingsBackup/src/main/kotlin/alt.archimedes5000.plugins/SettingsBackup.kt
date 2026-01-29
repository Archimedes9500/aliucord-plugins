package alt.archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.Utils.showToast
import com.aliucord.SettingsUtilsJSON
import org.json.*
import java.lang.reflect.*

import com.aliucord.Utils
import com.aliucord.utils.ReflectUtils
import com.aliucord.patcher.*

import com.aliucord.utils.GsonUtils
import com.google.gson.reflect.TypeToken

import android.content.SharedPreferences
import com.discord.stores.StoreStream
import com.discord.stores.StoreAuthentication
import com.discord.utilities.persister.Persister
import java.lang.ref.WeakReference

import com.discord.stores.StoreNux

@AliucordPlugin(requiresRestart = true)
class SettingsBackup: Plugin(){
	@SuppressLint("SetTextI18n")

	val backup = SettingsUtilsJSON("Discord");
	fun optPersisters(): ArrayList<PersisterPartial<*>>?{
		val output = ArrayList<PersisterPartial<*>>();
		val array = backup.getJSONArray(key, null);
		if(array != null){
			for(i in 0 until array.length()){
				output.add(
					deserializePersister(array.getJSONObject(i))
				);
			};
			return output;
		}else{
			return null;
		};
	};
	fun <T>deserializePersister(obj: JSONObject): PersisterPartial<T>{
		val type = obj.getString("type");
		val key = obj.getString("key");

		val valueClass: Class<T> = Class.forName(type) as Class<T>;
		val valueString = obj.getString("value");

		val value = GsonUtils.fromJson(valueString, valueClass as Type) as T;
		return PersisterPartial<T>(type, key, value);
	};
	val fPersisterValue = Persister::class.java
		.getDeclaredField("value")
		.apply{isAccessible = true}
	;
	inline class PersisterPartial<reified T>(
		val type: String,
		val key: String,
		val value: T
	);
	inline fun <reified T>persisterPartial(p: Persister<T>): PersisterPartial<T>{
		return PersisterPartial<T>(
			T::class.java.toString(),
			p.getKey(),
			fPersisterValue.get(p) as T
		);
	);

	override fun start(pluginContext: Context){

		//auth settings
		val privateKeys = arrayOf(
			"LOG_CACHE_KEY_USER_LOGIN",
			"STORE_AUTHED_TOKEN",
			"STORE_AUTH_STATE",
			"LOG_CACHE_KEY_USER_ID",
			"LOG_CACHE_KEY_USER_NAME"
		);

		val storeAuth = StoreStream.getAuthentication();
		val editor: SharedPreferences.Editor = storeAuth.prefs.edit();

		//track number types
		patcher.patch(editor::class.java.getDeclaredMethod("putInt", String::class.java, Int::class.java), PreHook{frame ->
			val key = frame.args[0] as String;
			val types = backup.getJSONObject("types", JSONObject())!!;
			types.put(key, "Int");
			backup.setJSONObject("types", types);
		});
		patcher.patch(editor::class.java.getDeclaredMethod("putLong", String::class.java, Long::class.java), PreHook{frame ->
			val key = frame.args[0] as String;
			val types = backup.getJSONObject("types", JSONObject())!!;
			types.put(key, "Long");
			backup.setJSONObject("types", types);
		});
		patcher.patch(editor::class.java.getDeclaredMethod("putFloat", String::class.java, Float::class.java), PreHook{frame ->
			val key = frame.args[0] as String;
			val types = backup.getJSONObject("types", JSONObject())!!;
			types.put(key, "Float");
			backup.setJSONObject("types", types);
		});

		//import from backup
		val auth = backup.getObject("auth", mutableMapOf<String, Any>());
		val types = backup.getJSONObject("types", JSONObject())!!;
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
					else -> logger.debug("rejected key: $key\nof class: ${value::class}");
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
				val currentAuth = storeAuth.prefs.all.toMutableMap();
				if(!exposePrivate){
					for(key in privateKeys){
						currentAuth.remove(key);
					};
				};
				backup.setObject("auth", currentAuth);
			};
		};

		//export current settings to backup as they change
		patcher.patch(editor::class.java.getDeclaredMethod("apply"), PreHook{frame ->
			val writeBackup = settings.getBool("write_backup", true);
			if(writeBackup){
				val currentAuth = storeAuth.prefs.all;
				backup.setObject("auth", currentAuth);
			};
		});
		patcher.patch(editor::class.java.getDeclaredMethod("commit"), PreHook{frame ->
			val writeBackup = settings.getBool("write_backup", true);
			if(writeBackup){
				val currentAuth = storeAuth.prefs.all;
				backup.setObject("auth", currentAuth);
			};
		});

		//other stores and settings
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
			"STORE_FAVORITES",
			"EMOJI_HISTORY_V4",
			"STICKER_HISTORY_V1",
			"CACHE_KEY_SELECTED_EXPRESSION_TRAY_TAB",
			"CACHE_KEY_APPLICATION_COMMANDS",
			"CACHE_KEY_PHONE_COUNTRY_CODE_V2",
			"CONTACT_SYNC_DISMISS_STATE",
			"hub_verification_clicked_key",
			"guild_scheduled_events_header_dismissed",
			"hub_name_prompt"
		);

		//import from backup
		val persisters: Map<String, PersisterPartial<*>>? = optPersisters()
			?.mapNotNull{it.key to it}
			?.toMap()
		;
		if(persisters != null && !persisters.isEmpty()){
			patcher.patch(Persister::class.java.getDeclaredMethod("get"), PreHook{frame ->
				val original = frame.thisObject as Persister<*>;
				val replacement = persisters[original.getKey()];
				if(replacement != null){
					frame.result = fPersisterValue.get(replacement);
				};
			});
		}else{
			//export current settings to backup
			val currentPersisters = Persister.`access$getPreferences$cp`()
				.mapNotNull{(it as WeakReference<Persister<*>>).get()}
				.filter{it.getKey() in storeKeys}
				.map{persisterPartial(it)}
				as ArrayList<PersisterPartial<*>>
			;
			backup.setObject("persisters", currentPersisters);
		};

		//export current settings to backup as they change
		patcher.patch(Persister::class.java.getDeclaredMethod("set", Any::class.java, Boolean::class.java), PreHook{frame ->
			val _this = frame.thisObject as Persister<*>;
			if(_this.getKey() in storeKeys){
				val currentPersisters = Persister.`access$getPreferences$cp`()
					.mapNotNull{(it as WeakReference<Persister<*>>).get()}
					.filter{it.getKey() in storeKeys}
					.map{persisterPartial(it)}
					as ArrayList<PersisterPartial<*>>
				;
				backup.setObject("persisters", currentPersisters);
			};
		});

	};
	override fun stop(pluginContext: Context) = patcher.unpatchAll();
};