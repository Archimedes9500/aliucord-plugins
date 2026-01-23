package alt.archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.Utils.showToast
import com.aliucord.SettingsUtilsJSON
import org.json.*

import com.aliucord.Utils
import com.aliucord.patcher.*

import android.content.SharedPreferences
import com.discord.stores.StoreStream
import com.discord.stores.StoreAuthentication

@AliucordPlugin(requiresRestart = true)
class SettingsBackup: Plugin(){
	@SuppressLint("SetTextI18n")
	val settings2 = SettingsUtilsJSON("Discord");

	override fun start(pluginContext: Context){

		val storeAuth = StoreStream.getAuthentication();
		val editor: SharedPreferences.Editor = storeAuth.prefs.edit();
		val backupSettings = settings.getObject<Map<String, Any?>>("settings", mapOf());
		if(!backupSettings.isEmpty()){
			for((key, value) in backupSettings){
				when(value){
					is String -> editor.putString(key, value);
					is Int -> editor.putInt(key, value);
					is Boolean -> editor.putBoolean(key, value);
					is Float -> editor.putFloat(key, value);
					is Long -> editor.putLong(key, value);
					is Set<*> -> if(value.all{it is String}) editor.putStringSet(key, value as Set<String>);
				};
			};
		};

		patcher.before<SharedPreferences>(
			"apply"
		){frame ->
			val prefs = frame.thisObject as SharedPreferences;

			val currentSettings = prefs.all;
			settings2.setObject("settings", currentSettings);
		};

		patcher.before<SharedPreferences>(
			"commit"
		){frame ->
			val pref = frame.thisObject as SharedPreferences;

			val currentSettings = pref.all;
			settings2.setObject("settings", currentSettings);
		};
/*
		patcher.after<StoreAuthentication>(
			"handleAuthState$app_productionGoogleRelease",
			AuthState::class.java
		){frame ->
			val storeAuth = frame.thisObject as StoreAuthentication;

			val backupSettings = settings.getObject<Map<String, Any?>>("backup", mapOf());
			if(backupSettings.isEmpty()) return@after;
			val editor: SharedPreferences.Editor = storeAuth.prefs.edit();
			
		};
*/
	};
	override fun stop(pluginContext: Context) = patcher.unpatchAll();
};