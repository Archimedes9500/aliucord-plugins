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

	inline fun <reified T>optNotRetarded(key: String): T?{
		val string = settings2.getString(key, null);
		if(string != null){
			return GsonUtils.fromJson(string, T::class.java as Type) as T?;
		}else{
			return null;
		};
	};

	val fStoreFavorites = StoreEmoji::class.java
		.getDeclaredField("mediaFavoritesStore")
		.apply{isAccessible = true}
	;
	val fObservationDeck = StoreMediaFavorites::class.java
		.getDeclaredField("observationDeck")
		.apply{isAccessible = true}
	;
	val fDispatcher = StoreMediaFavorites::class.java
		.getDeclaredField("dispatcher")
		.apply{isAccessible = true}
	;
	val fFavorites = StoreMediaFavorites::class.java
		.getDeclaredField("persister")
		.apply{isAccessible = true}
	;
	fun parseFavorites(set: Set<JSONObject>?): Set<out Favorite>?{
		if(set == null) return null;
		return set.mapNotNull{
			val id = it.optString("emojiUniqueId");
			if(id == null || id == "") return@mapNotNull null;
			logger.debug("creating emoji with id: "+id::class);
			if(id.toLongOrNull() != null){
				FavCustomEmoji(id);
			}else{
				FavUnicodeEmoji(id);
			};
		}.toSet();
	};
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
/*
		val rawFavorites = settings2.getObject("""emoji$favorite""", Set<JSONObject>());
		val favorites: Set<out Favorite>? = parseFavorites(rawFavorites);
		logger.debug("favorites: "+favorites?.joinToString(", ")?: "null");

		val storeFavorites = fStoreFavorites.get(storeEmoji) as StoreMediaFavorites;
		val currentFavorites = StoreMediaFavorites.`access$getFavorites$p`(storeFavorites) as Set<Favorite>;
		logger.debug("currentFavorites: "+currentFavorites.joinToString(", "));
		if(favorites != null){
			for(favorite in currentFavorites){
				logger.debug("removing favorite: "+favorite.toString());
				storeFavorites.removeFavorite(favorite);
			};
			for(favorite in favorites){
				logger.debug("adding favorite: "+favorite.toString());
				storeFavorites.addFavorite(favorite);
			};
		}else{
			emoji.favorite = currentFavorites;
		};
		settings2.setObject("""emoji$favorites""", favorites);
*/
		val frequentEmoji: Persister<MediaFrecencyTracker>? = optNotRetarded("emoji\$frequent");
		if(frequentEmoji != null){
			Persister.`access$persist`(frequentEmoji);
			Persister.`access$persist`(fFrequentEmoji.get(storeEmoji));
			fFrequentEmoji.set(storeEmoji, frequentEmoji);
		}else{
			settings2.setObject("emoji\$frequent", fFrequentEmoji.get(storeEmoji) as Persister<MediaFrecencyTracker>);
		};
/*
		patcher.patch(editor::class.java.getDeclaredMethod("apply"), PreHook{frame ->
			val currentAuth = storeAuth.prefs.all;
			settings2.setObject("auth", currentAuth);
		});

		patcher.patch(editor::class.java.getDeclaredMethod("commit"), PreHook{frame ->
			val currentAuth = storeAuth.prefs.all;
			settings2.setObject("auth", currentAuth);
		});
*/
	};
	override fun stop(pluginContext: Context) = patcher.unpatchAll();
};