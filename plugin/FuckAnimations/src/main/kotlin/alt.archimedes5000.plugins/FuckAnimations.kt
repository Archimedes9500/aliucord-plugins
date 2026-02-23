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
/*
	val fReducedMotionEnabled = StoreAccessibility::class.java
		.getDeclaredField("reducedMotionEnabled")
		.apply{isAccessible = true}
	;
*/
	lateinit var originalState: Boolean;

	override fun start(pluginContext: Context){
		originalState = settings.getBool(
			"originalState",
			StoreStream.getAccessibility().getReducedMotionEnabled()
		);
		//Force enable reduced motion
		store.setReducedMotionEnabled(true);
/*
		patcher.after<StoreStream.Companion>("getAccessibility"){frame ->
			val store = frame.result as StoreAccessibility;
			fReducedMotionEnabled.set(store, true);
			frame.result = store;
		};
		patcher.after<StoreAccessibility>(
			"access\$getReducedMotionEnabled\$p",
			StoreAccessibility::class.java
		){frame ->
			logger.debug("access$"+frame.result.toString());
			frame.result = false;
		};
		patcher.after<StoreAccessibility>(
			"getReducedMotionEnabled"
		){frame ->
			logger.debug(frame.result.toString());
			frame.result = false;
		};
*/
		//Ignore reduced motion for those cases
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
		StoreStream.getAccessibility().setReducedMotionEnabled(
			settings.getBool("originalState", originalState?: false)
		);
		patcher.unpatchAll();
	};
};
