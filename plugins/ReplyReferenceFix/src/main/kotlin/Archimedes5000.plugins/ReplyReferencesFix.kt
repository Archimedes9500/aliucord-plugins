package Archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import java.lang.reflect.InvocationTargetException
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.patcher.Hook
import com.aliucord.Utils
import com.discord.stores.StoreStream
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.aliucord.Logger

@AliucordPlugin(requiresRestart = false)
class ReplyReferencesFix:Plugin(){
	@SuppressLint("SetTextI18n")
	override fun start(context:Context){
		with(WidgetChatListAdapter::class.java){
			patcher.patch( //setting listeners
				getDeclaredMethod(
					"onConfigure",
					WidgetChatListAdapter::class.java
				),
				Hook{
					frame ->
					val item = frame.thisObject as WidgetChatListAdapterItemMessage;
					Logger().debug(frame.toString());
					val replyViewID = Utils.getResId(
						"chat_list_adapter_item_text_decorator",
						"id"
					);
					val iconViewID = Utils.getResId(
						"chat_list_adapter_item_text_decorator_reply_link_icon",
						"id"
					);
					if(
						item.itemView.id == replyViewID
						|| item.itemView.id == iconViewID
					){
						item.itemView.setOnClickListener{
							try{
								var msg =
									(
										frame.args.message
										as WidgetChatListAdapterItemMessage
									)
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
					}
				}
			)
		}
	}
	override fun stop(context:Context) = patcher.unpatchAll();
}