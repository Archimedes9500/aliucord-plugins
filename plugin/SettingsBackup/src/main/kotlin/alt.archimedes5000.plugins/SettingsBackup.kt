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
import com.discord.stores.Store
import com.discord.stores.StoreStream

@AliucordPlugin(requiresRestart = true)
class SettingsBackup: Plugin(){
	@SuppressLint("SetTextI18n")
	val settings2 = SettingsUtilsJSON("Discord");

	override fun start(pluginContext: Context){

		val storeAuth = StoreStream.getAuthentication();
		val editor: SharedPreferences.Editor = storeAuth.prefs.edit();
		val auth = settings2.getObject<Map<String, Any?>>("auth", mapOf());
		if(!auth.isEmpty()){
			for((key, value) in auth){
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
			val currentAuth = storeAuth.prefs.all;
			settings2.setObject("auth", currentAuth);
		};

		val storeEmoji = StoreStream.getEmojis();
		val fFavoriteEmoji = storeEmoji::class.java.getDeclaredField("storeMediaFavorites");
		val fFrequentEmoji = storeEmoji::class.java.getDeclaredField("frecencyCache");

		var emoji: Map<String, Any> = settings.getObject("emoji", mapOf());

		val favoriteEmoji = emoji["favorite"];
		if(favoriteEmoji != null){
			fFavoriteEmoji.set(storeEmoji, favoriteEmoji);
		}else{
			emoji.put("favorite", fFavoriteEmoji.get(storeEmoji));
		};
		val frequentEmoji = emoji["frequent"];
		if(frequentEmoji != null){
			fFavoriteEmoji.set(storeEmoji, frequentEmoji);
		}else{
			emoji.put("frequent", fFrequentEmoji.get(storeEmoji));
		};
		settings2.setObject("emoji", emoji);

		patcher.patch(editor::class.java.getDeclaredMethod("apply"), PreHook{frame ->
			val currentAuth = storeAuth.prefs.all;
			settings2.setObject("auth", currentAuth);
		});

		patcher.patch(editor::class.java.getDeclaredMethod("commit"), PreHook{frame ->
			val currentAuth = storeAuth.prefs.all;
			settings2.setObject("auth", currentAuth);
		});
	};
	override fun stop(pluginContext: Context) = patcher.unpatchAll();
};