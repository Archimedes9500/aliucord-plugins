package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.discord.widgets.chat.list.actions.WidgetChatListActions;
import com.discord.widgets.chat.list.actions.WidgetChatListActions.Model;
import com.discord.databinding.WidgetChatListActionsBinding;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import com.aliucord.wrappers.ChannelWrapper;
import com.discord.stores.StoreStream;

@AliucordPlugin(requiresRestart = true)
class MessageAnchor: Plugin(){
	val WidgetChatListActions.binding: WidgetChatListActionsBinding by accessGetter();

	override fun start(pluginContext: Context){
		patcher.after<WidgetChatListActions>(
			"configureUI",
			Model::class.java
		){frame ->
			val saveView = binding
				.a
				.findViewById<TextView>("dialog_chat_actions_profile")
				.apply{
					visibility = View.VISIBLE;
				}
			;
			saveView.setOnLongClickListener{
				var msg = (frame.args[0] as Model).message;
				settings.setLong(
					msg.channelId.toString(),
					msg.id
				);
				showToast("Anchor saved", showLonger = false);
				dismiss();
				true;
			};

			val jumpView = binding
				.a
				.findViewById<TextView>("dialog_chat_actions_reply")
				.apply{
					visibility = View.VISIBLE;
				}
			;
			jumpView.setOnLongClickListener{
				var channelID = ChannelWrapper((frame.args[0] as Model).channel).id;
				var messageID = settings.getLong(
					channelID.toString(),
					84115L
				);
				StoreStream.getMessagesLoader().jumpToMessage(channelID, messageID);
				showToast("Jumped to anchor", showLonger = false);
				dismiss();
				true;
			};

			val test = binding
				.a
				.findViewById<ViewGroup>("action_bar_toolbar")
				.apply{
					visibility = View.VISIBLE;
				}
				.getChildAt(0)
				.apply{
					visibility = View.VISIBLE;
					setBackgroundColor(android.graphics.Color.RED);
					isLongClickable = true;
					setOnLongClickListener{
						logger.debug("balls");
						true;
					};
				}
			;
		};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};