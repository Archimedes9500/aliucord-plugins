package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemSystemMessage;
import com.discord.widgets.chat.list.entries.ChatListEntry;
import com.discord.databinding.WidgetChatListAdapterItemSystemBinding;
import com.discord.widgets.chat.list.entries.MessageEntry;

@AliucordPlugin(requiresRestart = true)
class SystemProfiles: Plugin(){
	val WidgetChatListAdapterItemSystemMessage.binding by
		accessField<WidgetChatListAdapterItemSystemBinding>()
	;
	override fun start(pluginContext: Context){
		patcher.after<WidgetChatListAdapterItemSystemMessage>(
			"onConfigure",
			Int::class.java,
			ChatListEntry::class.java
		){frame ->
			binding.f/*system_icon*/!!.apply{
				isClickable = true;
				setOnClickListener{
					val message = (frame.args[1] as MessageEntry).message;
					WidgetChatListAdapterItemSystemMessage
						.`access$getAdapter$p`(this@after)
						.getEventHandler()
						.onMessageAuthorAvatarClicked(message, message.guildId?: 0)
					;
				};
			};
		};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};