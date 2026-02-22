package alt.archimedes5000.plugins

import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.patcher.*

import com.discord.stores.StoreStream
import com.discord.stores.StoreAccessibility
import com.discord.stores.StoreUserSettings

typealias IntIterator = d0.t.c0;
typealias IntProgressionIterator = d0.d0.b;

@AliucordPlugin(requiresRestart = true)
@SuppressLint("SetTextI18n")
class FuckAnimations: Plugin(){
	override fun start(pluginContext: Context){
	val fReducedMotionEnabled = StoreAccessibility::class.java
		.getDeclaredField("reducedMotionEnabled")
		.apply{isAccessible = true}
	;
/*
		patcher.instead<InlineMediaView>("shouldAutoPlay"){frame ->
			return@instead true;
		};
*/
		patcher.after<StoreStream>("getAccessibility"){frame ->
			val store = frame.result as StoreAccessibility;
			fReducedMotionEnabled.set(store, false);
			frame.result = store;
		};

		patcher.before<StoreUserSettings>(
			"observeIsAnimatedEmojisEnabled",
			Boolean::class.java
		){frame ->
			frame.args[0] = false;
		};
		patcher.before<StoreUserSettings>(
			"observeIsAutoPlayGifsEnabled",
			Boolean::class.java
		){frame ->
			frame.args[0] = false;
		};
		patcher.before<StoreUserSettings>(
			"observeStickerAnimationSettings",
			Boolean::class.java
		){frame ->
			frame.args[0] = false;
		};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};