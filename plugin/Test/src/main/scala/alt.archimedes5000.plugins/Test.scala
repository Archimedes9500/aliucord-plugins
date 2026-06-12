package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.UtilsKt._;
import com.aliucord.utils._;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.PatcherExtensionsKt._;

import com.discord.stores.StoreMessages;
import com.discord.stores.StoreMessagesLoader.ChannelChunk;
import com.discord.models.message.Message;
/*
var delegateMessageContent: String by UtilsKt.accessFinalField("content");
implicit class MessageWrapper(val m: Message) extends AnyVal{
	def _content: String = {
		delegateMessageContent.getValue(m, "content").asInstanceOf[String];
	};
	def _content_=(v: String): Unit = {
		delegateMessageContent.setValue(m, "content", v);
	};
};
*/
@AliucordPlugin(requiresRestart = true)
class Test extends Plugin(){
	val fContent = classOf[Message].getDeclaredField("content");
	@Override def start(pluginContext: Context){
		before[StoreMessages](patcher,
			"handleMessagesLoaded",
			classOf[ChannelChunk]
		){frame =>
			val chunk = frame.args(0).asInstanceOf[ChannelChunk];
			for(m <- chunk.messages){
				fContent.set("balls");
			};
		};
	};
	@Override def stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};