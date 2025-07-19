package Archimedes5000.plugins
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.discord.databinding.WidgetChatListActionsBinding
import com.discord.widgets.chat.list.actions.WidgetChatListActions
import com.lytefast.flexinput.R
import java.lang.reflect.InvocationTargetException
import com.aliucord.api.SettingsAPI
import com.aliucord.Utils.showToast
import com.discord.app.AppBottomSheet

@AliucordPlugin(requiresRestart = false)
class MessageLinkContext : Plugin(){
	@SuppressLint("SetTextI18n")
	val settings = SettingsAPI("Shit");
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
					val copyViewID = Utils.getResId(
						"dialog_chat_actions_copy_id",
						"id"
					);
					val copyView = binding.a
						.findViewById<TextView>(copyViewID)
						.apply {
							visibility = View.VISIBLE
						}
					;
					try{
						copyView.setOnClickListener{
							try{
								var msg =
									(
										callFrame.args[0]
										as WidgetChatListActions.Model
									)
									.message
								;
								settings.setString(
									msg.channelId.toString(),
									msg.id.toString()
								);
								Utils.setClipboard(
									"null",
									msg.id.toString()
								);
								showToast(
									"Copied to clipboard",
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
							}catch(e: IllegalAccessException){
								e.printStackTrace();
							}catch(e: InvocationTargetException){
								e.printStackTrace();
							}
						}
					}catch(e: Exception){ //yes generic maybe works idk
						e.printStackTrace();
					}
					val topChannelViewID = Utils.getResId(
						"action_bar_toolbar_layout",
						"id"
					);
					val topChannelView = binding.a
						.findViewById<TextView>(topChannelViewID)
						.apply {
							visibility = View.VISIBLE
						}
					;
					try{
						topChannelView.setOnLongClickListener{
							showToast(
								"It works",
								showLonger = false
							);
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