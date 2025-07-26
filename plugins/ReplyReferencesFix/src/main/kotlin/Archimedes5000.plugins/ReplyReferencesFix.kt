package Archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import java.lang.reflect.InvocationTargetException
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.patcher
import com.aliucord.Utils
import com.discord.stores.StoreStream
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.entries.ChatListEntry
import com.aliucord.Logger

@AliucordPlugin(requiresRestart = false)
class ReplyReferencesFix:Plugin(){
	@SuppressLint("SetTextI18n")
	override fun start(context:Context){
		patcher.after<WidgetChatListAdapterItemMessage>(
            "onConfigure",
            Int::class.java,
            ChatListEntry::class.java
        ){
			val replyViewID = Utils.getResId(
				"chat_list_adapter_item_text_decorator",
				"id"
			);
			val iconViewID = Utils.getResId(
				"chat_list_adapter_item_text_decorator_reply_link_icon",
				"id"
			);
			Logger().debug(this.toString());
			/*if(
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
			}*/
		}
	}
	override fun stop(context:Context) = patcher.unpatchAll();
}