package Archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import java.lang.reflect.InvocationTargetException
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.patcher.Hook
import com.aliucord.Utils
import com.aliucord.Utils.showToast
import com.discord.stores.StoreStream
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage

@AliucordPlugin(requiresRestart = false)
class ReplyReferencesFix: Plugin(){
	@SuppressLint("SetTextI18n")
	override fun start(context: Context){
		with(WidgetChatListAdapterItemMessage::class.java){
			val getBinding = getDeclaredMethod("getBinding")
				.apply{
					isAccessible = true
				}
			;
			patcher.patch( //setting listeners
				getDeclaredMethod(
					"onConfigure",
					WidgetChatListAdapterItemMessage::class.java
				),
				Hook{
					frame ->
					Utils.showToast(
						frame.toString(),
						showLonger = false
					);
					/*
					val replyViewID = Utils.getResId(
						"chat_list_adapter_item_text_decorator",
						"id"
					);
					val iconViewID = Utils.getResId(
						"chat_list_adapter_item_text_decorator_reply_link_icon",
						"id"
					);
					if(
						frame.thisObject.itemView == replyViewID
						|| frame.thisObject.itemView == iconViewID
					){
						frame.thisObject.itemView.setOnClickListener{
							try{
								var msg =
									(
										frame.args[0]
										as WidgetChatListAdapterItemMessage
									)
									.message
								;
								var t = msg.messageReference;
								StoreStream.getMessagesLoader()
									.jumpToMessage(t.channelID, t.messageID)
								;
							}catch(e:IllegalAccessException){
								e.printStackTrace();
							}catch(e:InvocationTargetException){
								e.printStackTrace();
							}
						}
					}*/
				}
			)
		}
	}
	override fun stop(context: Context) = patcher.unpatchAll();
}