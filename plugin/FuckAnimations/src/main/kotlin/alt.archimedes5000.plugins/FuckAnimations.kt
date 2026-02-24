package alt.archimedes5000.plugins

import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.patcher.*

import com.discord.stores.StoreUserSettings
import com.discord.utilities.persister.Persister
import com.discord.stores.StoreStream
import com.discord.stores.StoreAccessibility
import com.discord.widgets.chat.input.emoji.EmojiPickerViewModel

typealias IntIterator = d0.t.c0;
typealias IntProgressionIterator = d0.d0.b;

@AliucordPlugin(requiresRestart = false)
@SuppressLint("SetTextI18n")
class FuckAnimations: Plugin(){
	var originalState: Boolean? = null;

	override fun start(pluginContext: Context){
		val fPersisterValue = Persister::class.java
			.getDeclaredField("value")
			.apply{isAccessible = true}
		;
		val fpAnimatedEmojis = StoreUserSettings::class.java
			.getDeclaredField("allowAnimatedEmojisPublisher")
			.apply{isAccessible = true}
		;

		val store = StoreStream.Companion!!.getAccessibility();
		originalState = store.isReducedMotionEnabled().also{
			logger.debug("originalState: $it");
			settings.setBool("originalState", it);
		};
		store.setReducedMotionEnabled(true);
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
		//And also this one
		patcher.before<EmojiPickerViewModel.StoreState.Emoji>(
			com.discord.models.domain.emoji.EmojiSet::class.java,
			com.discord.stores.StoreEmoji.EmojiContext::class.java,
			java.util.LinkedHashMap::class.java,
			String::class.java,
			Boolean::class.java,//allowEmojisToAnimate
			Long::class.java,
			java.util.Set::class.java
		){frame ->
			frame.args[4] = fPersisterValue.get(
				fpAnimatedEmojis.get(StoreStream.getUserSettings())
			);
		};
	};
	override fun stop(pluginContext: Context){
		StoreStream.Companion!!.getAccessibility().setReducedMotionEnabled(
			settings.getBool("originalState", originalState?: false)
		);
		patcher.unpatchAll();
	};
};