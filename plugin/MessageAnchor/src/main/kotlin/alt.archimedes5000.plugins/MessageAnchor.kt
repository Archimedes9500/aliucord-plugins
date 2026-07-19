package alt.archimedes5000.plugins;

import alt.archimedes5000.plugins.utils.*;
import com.aliucord.utils.*;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import android.content.Context;
import com.aliucord.patcher.*;

import com.discord.widgets.chat.list.actions.WidgetChatListActions;
import com.discord.databinding.WidgetChatListActionsBinding;
import android.widget.TextView;
import android.view.View;
import com.discord.app.AppBottomSheet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

@AliucordPlugin(requiresRestart = true)
class Template: Plugin(){
	override fun start(pluginContext: Context){
		val WidgetChatListActions.binding: WidgetChatListActionsBinding
			get() by accessMethod("getBinding")
		;

		fun WidgetChatListActions.dismiss() by accessMethod();
		patcher.after<WidgetChatListActions>(
			"configureUI",
			WidgetChatListActions.Model::class.java
		){frame ->
			val saveView: TextView = binding
				.a
				.findViewById("dialog_chat_actions_profile")
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
			};

			val jumpView: TextView = binding
				.a
				.findViewById("dialog_chat_actions_reply")
				.apply{
					visibility = View.VISIBLE;
				}
			;
			jumpView.setLongClickable(true);
			jumpView.setOnLongClickListener{
				var channelID = ChannelWrapper((frame.args[0] as Model).channel).id;
				var messageID = settings.getLong(
					channelID.toString(),
					84115L
				);
				StoreStream.getMessagesLoader().jumpToMessage(channelID, messageID);
				showToast("Jumped to anchor", showLonger = false);
				dismiss();
			};
		};
	};
	override fun stop(pluginContext: Context){
		patcher.unpatchAll();
	};
};