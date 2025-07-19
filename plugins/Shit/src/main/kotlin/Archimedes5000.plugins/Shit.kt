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
		lateinit var copyMessageUrlView : TextView;
		with(WidgetChatListActions::class.java){
			patcher.patch( //getting the option
				getDeclaredMethod(
					"onViewCreated",
					View::class.java,
					Bundle::class.java
				),
				Hook{
					callFrame ->
					var layout =
						(
							callFrame.args[0]
							as NestedScrollView
						)
						.getChildAt(0)
						as LinearLayout
					;
					for(i in 0 until layout.getChildCount()){
						var v = layout.getChildAt(i);
						if(v is TextView){
							var text = v.getText() as CharSequence;
							if("Copy Link".contentEquals(text)){
								copyMessageUrlView = v;
							}
						}
					}
				}
			)
			patcher.patch( //setting onClickListener
				getDeclaredMethod(
					"configureUI",
					WidgetChatListActions.Model::class.java
				),
				Hook{
					callFrame ->
					try{
						copyMessageUrlView.setOnClickListener{
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
								val guild =
									(
										callFrame.args[0]
										as WidgetChatListActions.Model
									)
									.guild
								; //because msg.guildId is null
								val messageUri = String.format(
									"https://discord.com/channels/%s/%s/%s",
									try{
										guild.id
									}catch(e: Throwable){ //for DMs
										"@me"
									},
									msg.channelId,
									msg.id
								);
								Utils.setClipboard(
									"message link",
									messageUri
								);
								showToast("Copied url", showLonger = false)
								val bottomSheetDismisser = AppBottomSheet
									::class.java
									.getDeclaredMethod("dismiss") //because cannot access shit again
								bottomSheetDismisser.invoke(
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
				}
			)
		}
	}
	override fun stop(context: Context) = patcher.unpatchAll();
}