package Archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin
import android.annotation.SuppressLint
import java.lang.reflect.InvocationTargetException
import com.aliucord.entities.Plugin
import android.content.Context
import com.aliucord.patcher.after
import com.aliucord.Utils
import com.discord.stores.StoreStream
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.adapter.`WidgetChatListAdapterItemMessage$onConfigure$3`
import com.aliucord.Logger
import android.view.View
import android.widget.FrameLayout
import com.discord.models.message.Message

@AliucordPlugin(requiresRestart = false)
class ReplyReferencesFix:Plugin(){
	@SuppressLint("SetTextI18n")
	override fun start(context:Context){
		patcher.after<`WidgetChatListAdapterItemMessage$onConfigure$3`>(
			WidgetChatListAdapterItemMessage::class.java,
			Message::class.java,
			Boolean::class.java
		){
			val adapter = this.`this$0` as WidgetChatListAdapterItemMessage;
			val message = this.`$message` as Message;
			val rootView = adapter.itemView as View;
			Logger().debug(adapter.toString());
			Logger().debug(message.toString());
			val replyViewID = Utils.getResId(
				"chat_list_adapter_item_text_decorator",
				"id"
			);
			val iconViewID = Utils.getResId(
				"chat_list_adapter_item_text_decorator_reply_link_icon",
				"id"
			);
			val replyView = rootView
				.findViewById<FrameLayout>(replyViewID)
				.apply{
					visibility = View.VISIBLE;
				}
			;
			val iconView = rootView
				.findViewById<FrameLayout>(iconViewID)
				.apply{
					visibility = View.VISIBLE;
				}
			;
			/*if(
				item.itemView.id == replyViewID
				|| item.itemView.id == iconViewID
			){
				this.itemView.setOnClickListener{
					try{
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