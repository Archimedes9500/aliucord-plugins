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
import android.view.ViewGroup
import android.widget.FrameLayout
import com.discord.models.message.Message
import com.discord.api.message.MessageReference;

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
			val elements = arrayOf(
				"chat_list_adapter_item_text_decorator",
				"chat_list_adapter_item_text_decorator_reply_link_icon",
				"chat_list_adapter_item_text_root",//test
			);
			for(viewRes in elements){
				val viewID = Utils.getResId(viewRes, "id");
				val view = rootView
					.findViewById<View?>(viewID)
					.apply{
						visibility = View.VISIBLE;
					}
				;
				if(view != null && message.messageReference != null){
					//Logger().debug(adapter.toString());
					Logger().debug(message.referencedMessage.toString());
					view.setOnClickListener{
						try{
							var target = message.messageReference as MessageReference;
							StoreStream.getMessagesLoader()
								.jumpToMessage(target.a(), target.c())
							;
							Utils.showToast("Yay", showLonger = false);
						}catch(e:IllegalAccessException){
							e.printStackTrace();
						}catch(e:InvocationTargetException){
							e.printStackTrace();
						}
					}
				}else{
				}
			}
		}
	}
	override fun stop(context:Context) = patcher.unpatchAll();
}