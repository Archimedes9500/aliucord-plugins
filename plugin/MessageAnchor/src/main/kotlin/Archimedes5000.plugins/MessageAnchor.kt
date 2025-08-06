package Archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import java.lang.reflect.InvocationTargetException
import com.aliucord.entities.Plugin
import android.content.Context
import com.discord.widgets.chat.list.actions.WidgetChatListActions
import com.discord.databinding.WidgetChatListActionsBinding
import com.aliucord.patcher.Hook
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.Utils.showToast
import com.aliucord.api.SettingsAPI
import com.aliucord.wrappers.ChannelWrapper
import com.discord.stores.StoreStream
import com.discord.app.AppBottomSheet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

@AliucordPlugin(requiresRestart = false)
class MessageAnchor : Plugin(){
	@SuppressLint("SetTextI18n")
	val settings = SettingsAPI("MessageAnchor");
	override fun start(context: Context){
		with(WidgetChatListActions::class.java){
			val getBinding = getDeclaredMethod("getBinding")
				.apply{
					isAccessible = true
				}
			;
			patcher.patch( //setting listeners
				getDeclaredMethod(
					"configureUI",
					WidgetChatListActions.Model::class.java
				),
				Hook{
					callFrame ->
					val binding = getBinding
						.invoke(callFrame.thisObject)
						as WidgetChatListActionsBinding
					;
					val saveViewID = Utils.getResId(
						"dialog_chat_actions_profile",
						"id"
					);
					val saveView = binding.a
						.findViewById<TextView>(saveViewID)
						.apply{
							visibility = View.VISIBLE;
						}
					;
					try{
						saveView.setOnLongClickListener{
							try{
								var msg =
									(
										callFrame.args[0]
										as WidgetChatListActions.Model
									)
									.message
								;
								settings.setLong(
									msg.channelId.toString(),
									msg.id
								);
								showToast(
									"Anchor saved",
									showLonger = false
								);
								val dismisser = AppBottomSheet::class.java
									.getDeclaredMethod("dismiss")
								; //because cannot access shit again
								dismisser.invoke(
									(
										callFrame.thisObject
										as WidgetChatListActions
									)
								);
								true;
							}catch(e: IllegalAccessException){
								e.printStackTrace();
								false;
							}catch(e: InvocationTargetException){
								e.printStackTrace();
								false;
							}
						}
					}catch(e: Exception){ //yes generic maybe works idk
						e.printStackTrace();
					}
					val jumpViewID = Utils.getResId(
						"dialog_chat_actions_reply",
						"id"
					);
					val jumpView = binding.a
						.findViewById<TextView>(jumpViewID)
						.apply{
							visibility = View.VISIBLE;
						}
					;
					try{
						jumpView.setLongClickable(true);
						jumpView.setOnLongClickListener{
							var channelID =
								ChannelWrapper(
									(
										callFrame.args[0]
										as WidgetChatListActions.Model
									)
									.channel
								)
								.id
							;
							var messageID =
								settings.getLong(
									channelID.toString(),
									84115L
							);
							StoreStream.getMessagesLoader()
								.jumpToMessage(channelID, messageID)
							;
							showToast(
								"Jumped to anchor",
								showLonger = false
							);
							val dismisser = AppBottomSheet::class.java
								.getDeclaredMethod("dismiss")
							; //because cannot access shit again
							dismisser.invoke(
								(
									callFrame.thisObject
									as WidgetChatListActions
								)
							);
							true;
						}
					}catch(e: Exception){ //yes generic maybe works idk
						e.printStackTrace();
					}
				}
			)
		}
	}
	override fun stop(context: Context) = patcher.unpatchAll();
}