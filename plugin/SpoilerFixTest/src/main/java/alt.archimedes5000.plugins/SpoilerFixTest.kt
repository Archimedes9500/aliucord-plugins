package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.discord.api.message.attachment.MessageAttachment;

@AliucordPlugin(requiresRestart = true)
class SpoilerFixTest: Plugin(){
	val MessageAttachment.flags: Int = accessField();

	override fun start(pluginContext: Context){
		patcher.after<MessageAttachment>("h"/*"isSpoiler"*/){frame ->
			val isSpoiler = frame.result as Boolean;
			if(isSpoiler) return@after;
			frame.result = (0 != (flags and 8));
		};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};