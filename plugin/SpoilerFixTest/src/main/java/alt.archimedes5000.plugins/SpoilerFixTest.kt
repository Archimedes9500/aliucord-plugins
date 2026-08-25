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
	val MessageAttachment.flags: Int by accessField();

	override fun start(pluginContext: Context){
		Patcher.addPatch(MessageAttachment::class.java.getDeclaredMethod("h"/*"isSpoiler"*/),
			Hook{frame ->
				val isSpoiler = frame.result as Boolean;
				if(isSpoiler || frame.thisObject == null) return@Hook;
				with(frame.thisObject as MessageAttachment){
					frame.result = (0 != (flags and 8));
				};
			}
		);
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};
