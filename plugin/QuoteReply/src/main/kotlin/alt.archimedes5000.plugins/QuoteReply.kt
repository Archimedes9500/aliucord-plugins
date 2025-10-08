package alt.archimedes9500.plugins

import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import android.annotation.SuppressLint
import android.content.Context
import com.discord.widgets.chat.list.actions.WidgetChatListActions
import com.discord.widgets.chat.list.actions.WidgetChatListActions.Model
import com.discord.widgets.chat.list.actions.`WidgetChatListActions$configureUI$14`
import com.discord.databinding.WidgetChatListActionsBinding
import com.aliucord.utils.ReflectDelegates.accessGetter
import com.aliucord.patcher.Hook
import com.aliucord.patcher.PreHook
import com.aliucord.utils.ViewUtils.findViewById
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.discord.R
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ImageView
import android.widget.TextView
import com.discord.widgets.chat.input.WidgetChatInput
import com.discord.widgets.chat.input.ChatInputViewModel
import com.discord.widgets.chat.input.ChatInputViewModel.ViewState
import com.discord.widgets.chat.MessageManager
import com.discord.widgets.chat.MessageContent
import kotlin.jvm.functions.Function1
import com.discord.models.message.Message
import com.discord.utilities.color.ColorCompat

@AliucordPlugin
class QuoteReply: Plugin(){
	var state: Boolean = false;
	var quoteText: CharSequence = "";

	val WidgetChatListActions.binding by accessGetter<WidgetChatListActionsBinding>("getBinding");

	@SuppressLint("SetTextI18n")
	override fun start(ctx: Context) {

		this.state = false;//settings.getBool("default", false);

		patcher.patch(
			WidgetChatListActions::class.java.getDeclaredMethod(
				"configureUI",
				WidgetChatListActions.Model::class.java
			),
			Hook { (frame, model: WidgetChatListActions.Model) ->
				val actions = frame.thisObject as WidgetChatListActions;
				var quote: CharSequence = quote(model.message);
				val replyButton = actions.binding.root.findViewById<View>("dialog_chat_actions_reply");
				replyButton.setOnClickListener{
					`WidgetChatListActions$configureUI$14`(actions, model);
					this.quoteText = quote;
				}
			}
		)

		val inflater = LayoutInflater.from(ctx);
		val quoteIcon = ContextCompat.drawable(Utils.appContext, R.e.ic_quote_white_a60_24dp);
		val quote: LinearLayout = inflater.inflate(R.layout.widget_chat_input).findViewById("chat_input_context_reply_mention_button");
		quote.setId(View.generateViewId());
		quote.getLayoutParams().addRule(RelativeLayout.LEFT_OF, R.id.chat_input_context_reply_mention_button);
		val drawable = quote.findViewById<ImageView>("chat_input_context_reply_mention_button_image")
		drawable.setImageDrawable(quoteIcon);
		val text = quote.findViewById<ImageView>("chat_input_context_reply_mention_button_text")
		quote.setOnClickListener{
			toggleButton();
			configureButton(drawable, text);
		};
	
		patcher.patch(
			WidgetChatInput::class.java.getDeclaredMethod(
				"access\$configureUI",
				WidgetChatInput::class.java,
				ChatInputViewModel.ViewState::class.java
			),
			Hook{(frame, input: WidgetChatInput, viewState: ChatInputViewModel.ViewState) ->
				val binding = WidgetChatInput.`access$getBinding$p`(input);
				binding.root.findViewById<RelativeLayout>("chat_input_context_bar").addView(quote, 1);
			}
		)

		patcher.patch(
			ChatInputViewModel::class.java.getDeclaredMethod(
				"sendMessage",
				Context::class.java,
				MessageManager::class.java,
				MessageContent::class.java,
				List::class.java,
				Boolean::class.javaPrimitiveType,
				Function1::class.java
			),
			PreHook{(frame, _: Context, _: MessageManager, messageContent: MessageContent) ->
				val (text: String, users: List<User>) = messageContent;
				if(false/*settings.getBool("replace", false)*/){
					frame.args[2] = MessageContent(this.quoteText, users);
				}else{
					frame.args[2] = MessageContent(this.quoteText+text, users)
				}
			}
		)
	}

	@SuppressLint("SetTextI18n")
	private fun quote(msg: Message): CharSequence{
		val author = CoreUser(msg.author)
		var str: String = msg.content ?: "";
		if(hasMention()){
			str = "-# <@${author.id}> said:\n"
		}else{
			str = "-# **${author.username}** said:\n"
		}
		str.replace("\n", "\n>");
		str = str+"\n"
		return str as CharSequence;
	}

	fun toggleButton(){
		this.state = !this.state;
	}

	fun configureButton(drawable, text){
		val color = if(this.state){
			ColorCompat.getThemedColor(Utils.appContext, R.attr.colorControlBrandForeground);
		}else{
			ColorCompat.getThemedColor(Utils.appContext, R.attr.colorTextMuted);
		);
		ColorCompat.tintWithColor(drawable, color);
		text.setTextColor(color);
		text.setText(if(state) R.string.reply_mention_on else R.string.reply_mention_off);
	}

	override fun stop(ctx: Context) {
		patcher.unpatchAll()
	}

}