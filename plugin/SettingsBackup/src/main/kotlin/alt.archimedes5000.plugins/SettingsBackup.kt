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

import com.aliucord.utils.GsonUtils
import com.google.gson.reflect.TypeToken

import android.content.SharedPreferences
import com.discord.stores.StoreStream
import com.discord.stores.StoreAuthentication
import com.discord.stores.StoreEmoji
import com.discord.stores.StoreMediaFavorites
import com.discord.stores.StoreMediaFavorites.Favorite
import com.discord.stores.StoreMediaFavorites.Favorite.*
import com.discord.utilities.persister.Persister
import com.discord.utilities.media.MediaFrecencyTracker
import com.discord.stores.StoreNux

@AliucordPlugin(requiresRestart = true)
class SettingsBackup: Plugin(){
	@SuppressLint("SetTextI18n")
	val settings2 = SettingsUtilsJSON("Discord");

	val cUnsafe = Class.forName("sun.misc.Unsafe");
	val unsafe = cUnsafe
		.getDeclaredField("theUnsafe")
		.apply{isAccessible = true}
		.get(null)
	;
	val fFavoriteEmoji = StoreEmoji::class.java
		.getDeclaredField("mediaFavoritesStore")
		.apply{isAccessible = true}
	;
	val fFrequentEmoji = StoreEmoji::class.java
		.getDeclaredField("frecencyCache")
		.apply{isAccessible = true}
	;

	override fun start(pluginContext: Context){

		val storeAuth = StoreStream.getAuthentication();
		val editor: SharedPreferences.Editor = storeAuth.prefs.edit();
		val auth = settings2.getObject("auth", mutableMapOf<String, Any>());
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
		var emoji = settings2.getObject("emoji", mutableMapOf<String, Any>());
		val favoriteEmoji: Set<out Favorite>? =
			GsonUtils.fromJson<Set<JSONObject>>(
				GsonUtils.toJson(emoji["favorite"]),
				object: TypeToken<Set<JSONObject>>(){}.type
			)
			?.mapNotNull{
				val id = it.optString("emojiUniqueId")?: return@mapNotNull null;
				if(id.toLongOrNull() != null){
					FavCustomEmoji(id);
				}else{
					FavUnicodeEmoji(id);
				};
			}
			?.toSet()
		;
		logger.debug("favoriteEmoji: "+favoriteEmoji?.joinToString(", ")?: "");
		val storeFavorites = fFavoriteEmoji.get(storeEmoji) as StoreMediaFavorites;
		val currentFavorites: Set<Favorite> = StoreMediaFavorites
			.`access$getFavorites$p`(storeFavorites)
			.filter{(it as Favorite).emojiUniqueId != ""}
			.toSet()
		;
		logger.debug("currentFavorites: "+currentFavorites.joinToString(", ")?: "");
		if(favoriteEmoji != null){
			for(favorite in currentFavorites){
				storeFavorites.removeFavorite(favorite);
			};
			for(favorite in favoriteEmoji){
				logger.debug("favorite: "+favorite.toString());
				storeFavorites.addFavorite(favorite);
			};
		}else{
			emoji["favorite"] = currentFavorites;
		};
		val frequentEmoji: Persister<MediaFrecencyTracker>? = GsonUtils.fromJson(
			GsonUtils.toJson(emoji["frequent"]),
			object: TypeToken<Persister<MediaFrecencyTracker>>(){}.type
		);
		if(frequentEmoji != null){
			fFrequentEmoji.set(storeEmoji, frequentEmoji as Persister<MediaFrecencyTracker>);
		}else{
			emoji["frequent"] = fFrequentEmoji.get(storeEmoji) as Persister<MediaFrecencyTracker>;
		};
		settings2.setObject("emoji", emoji!!);

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