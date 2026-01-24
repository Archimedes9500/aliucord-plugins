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

		val stores = mapOf(
			"auth" to StoreStream::getAuthentication,
			"emojis" to StoreStream::getEmojis,
			"stickers" to StoreStream::getStickers,
			"nux" to StoreStream::getNux
		);
		for((storeKey, store) in stores){
			val editor = store().prefs.edit();
			val backupSettings = settings.getObject<Map<String, Any?>>(storeKey, mapOf());
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
			}else{
				val currentSettings = store().prefs.all;
				settings2.setObject(storeKey, currentSettings);
			};
			patcher.patch(editor::class.java.getDeclaredMethod("apply"), PreHook{frame ->
				val currentSettings = store().prefs.all;
				settings2.setObject(storeKey, currentSettings);
			});
	
			patcher.patch(editor::class.java.getDeclaredMethod("commit"), PreHook{frame ->
				val currentSettings = store().prefs.all;
				settings2.setObject(storeKey, currentSettings);
			});
		};
	};
	override fun stop(pluginContext: Context) = patcher.unpatchAll();
};