package alt.archimedes5000.plugins

import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import com.aliucord.entities.Plugin
import android.content.Context

import com.discord.stores.StoreMessagesHolder

typealias IntIterator = d0.t.c0;
typealias IntProgressionIterator = d0.d0.b;

@AliucordPlugin(requiresRestart = true)
@SuppressLint("SetTextI18n")
class MessageLoggerTest: PatchPlugin(){
	@Before
	StoreMessagesHolder.loadMessageChunks(
		list: List<StoreMessagesLoader.ChannelChunk>
	): Unit{
		logger.debug("\n\t"+list.joinToString("\n\t");
	};

	override fun start(pluginContext: Context){
		patcher.patchAll(this, pluginContext);
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};