package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.UtilsKt._;
import com.aliucord.utils._;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.PatcherExtensionsKt._;
import com.aliucord.PatcherAPI._;

import com.discord.stores.StoreMessages;
import com.discord.stores.StoreMessagesLoader.ChannelChunk;
import com.discord.models.message.Message;

@AliucordPlugin(requiresRestart = true)
class Test extends Plugin(){
	val fContent = classOf[Message].getDeclaredField("content");
	@Override def start(pluginContext: Context){
		PatcherExtensionsKt.before[StoreMessages](patcher,
			"handleMessagesLoaded",
			classOf[ChannelChunk]
		){frame =>
			val chunk = frame.args(0).asInstanceOf[ChannelChunk];
			for(m <- chunk.messages){
				fContent.set("balls from scala");
			};
		};
	};
	@Override def stop(pluginContext: Context){
		PatcherAPI.unpatchAll(patcher);
	};
};