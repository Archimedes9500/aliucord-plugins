package Archimedes5000.plugins;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.instead;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapter;
import com.discord.widgets.chat.list.adapter.WidgetChatListItem;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage;
import com.discord.widgets.chat.list.adapter.`WidgetChatListAdapterItemMessage$getMessageRenderContext$1`;
import com.discord.widgets.chat.list.adapter.`WidgetChatListAdapterItemMessage$getMessageRenderContext$2`;
import com.aliucord.patcher.component1;
import com.aliucord.patcher.component2;
import com.aliucord.utils.ReflectUtils;
import com.aliucord.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.LeadingMarginSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.core.text.BidiFormatter;
import androidx.core.view.ViewCompat;
import b.a.k.b;
import b.d.b.a.a;
import com.discord.R;
import com.discord.api.channel.Channel;
import com.discord.api.interaction.Interaction;
import com.discord.api.message.LocalAttachment;
import com.discord.api.permission.Permission;
import com.discord.api.role.GuildRole;
import com.discord.api.user.User;
import com.discord.api.utcdatetime.UtcDateTime;
import com.discord.app.AppLog;
import com.discord.models.member.GuildMember;
import com.discord.models.message.Message;
import com.discord.models.user.CoreUser;
import com.discord.nullserializable.NullSerializable;
import com.discord.stores.StoreMessageReplies;
import com.discord.stores.StoreMessageState;
import com.discord.stores.StoreStream;
import com.discord.stores.StoreUserSettings;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.guilds.PublicGuildUtils;
import com.discord.utilities.icon.IconUtils;
import com.discord.utilities.images.MGImages;
import com.discord.utilities.logging.Logger;
import com.discord.utilities.message.MessageUtils;
import com.discord.utilities.permissions.PermissionUtils;
import com.discord.utilities.textprocessing.DiscordParser;
import com.discord.utilities.textprocessing.MessagePreprocessor;
import com.discord.utilities.textprocessing.MessageRenderContext;
import com.discord.utilities.textprocessing.node.SpoilerNode;
import com.discord.utilities.time.TimeUtils;
import com.discord.utilities.user.UserUtils;
import com.discord.utilities.view.extensions.ViewExtensions;
import com.discord.utilities.view.text.SimpleDraweeSpanTextView;
import com.discord.views.FailedUploadList;
import com.discord.views.typing.TypingDots;
import com.discord.widgets.chat.list.ChatListItemMessageAccessibilityDelegate;
import com.discord.widgets.chat.list.entries.ChatListEntry;
import com.discord.widgets.chat.list.entries.MessageEntry;
import com.discord.widgets.chat.list.utils.EmbeddedMessageParser;
import com.discord.widgets.roles.RoleIconView;
import com.facebook.drawee.span.DraweeSpanStringBuilder;
import d0.t.n;
import d0.z.d.m;
import java.util.List;
import java.util.Map;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;

@AliucordPlugin(requiresRestart = false)
class ReplyReferencesFix:Plugin(){
	@SuppressLint("SetTextI18n")
	override fun start(context:Context){
		patcher.instead<WidgetChatListAdapterItemMessage>(
			Int::class.java,
			WidgetChatListAdapter::class.java
		){
			frame ->
			val i = frame.component1() as Int;
			val widgetChatListAdapter = frame.component2() as WidgetChatListAdapter;
			WidgetChatListItem(i, widgetChatListAdapter);
			val findViewById = this.itemView
				.findViewById(Utils.getResId("chat_list_adapter_item_text", "id"))
				as View
			;
			val itemText = findViewById as SimpleDraweeSpanTextView;
			val MAX_REPLY_AST_NODES = 50 as Int;
			val itemAvatar = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_avatar", "id")) as ImageView;
			val itemName = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_name", "id")) as TextView;
			val itemRoleIcon = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_role_icon", "id")) as RoleIconView;
			val itemTag = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_tag", "id")) as TextView;
			val replyHolder = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_decorator", "id")) as View;
			val replyLinkItem = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_decorator_reply_link_icon", "id")) as View;
			val replyLeadingViewsHolder = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_reply_leading_views", "id")) as View;
			val replyName = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_decorator_reply_name", "id")) as TextView;
			val replyIcon = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_decorator_reply_icon", "id")) as ImageView;
			val replyAvatar = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_decorator_avatar", "id")) as ImageView;
			val replyText = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_reply_content", "id")) as SimpleDraweeSpanTextView;
			val itemTimestamp = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_timestamp", "id")) as TextView;
			val failedUploadList = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_failed_upload_list", "id")) as FailedUploadList;
			val itemAlertText = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_alert_text", "id")) as TextView;
			val itemLoadingText = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_loading", "id")) as TextView;
			val backgroundHighlight = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_highlighted_bg", "id")) as View;
			val gutterHighlight = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_gutter_bg", "id")) as View;
			val loadingDots = this.itemView.findViewById(Utils.getResId("chat_overlay_typing_dots", "id")) as TypingDots;
			val sendError = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_text_error", "id")) as ImageView;
			val threadEmbedSpine = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_thread_embed_spine", "id")) as ImageView;
			val threadStarterMessageHeader = this.itemView.findViewById(Utils.getResId("thread_starter_message_header", "id")) as View;
			val communicationDisabledIcon = this.itemView.findViewById(Utils.getResId("chat_list_adapter_item_communication_disabled_icon", "id")) as ImageView;

			fun configureThreadSpine(message:Message, z2:Boolean):Void?{
				val imageView = threadEmbedSpine as ImageView?;
				if(imageView != null){
					imageView.setVisible(message.hasThread() && !z2);
				}
				return;
			}
			fun getAuthorTextColor(guildMember:GuildMember):Int?{
				val view = this.itemView as View;
				return GuildMember.Companion().getColor(
					guildMember,
					ColorCompat.getThemedColor(
						view.getContext(),
						Utils.getResId("colorHeaderPrimary", "attr") as Int
					)
				);
			}
			fun getLeadingEdgeSpan():LeadingMarginSpan{
				var i = null as Int?;
				val view = replyLeadingViewsHolder as View;
				if(view != null) {
					view.measure(0, 0);
					i = replyLeadingViewsHolder.getMeasuredWidth();
				}else{
					i = 0;
				}
				return LeadingMarginSpan.Standard(i, 0);
			}
			fun getMessagePreprocessor(j:Long?, message:Message, state:StoreMessageState.State):MessagePreprocessor{
				val userSettings = StoreStream
					.Companion()
					.getUserSettings()
					as StoreUserSettings
				;
				return MessagePreprocessor(
					j,
					state,
					if(
						!userSettings.getIsEmbedMediaInlined()
					||
						!userSettings.getIsRenderEmbedsEnabled()
					) null else message.getEmbeds(),
					true,
					null as Integer?
				);
			}
			fun getMessageRenderContext(
					context:Context,
					messageEntry:MessageEntry,
					function1:Function1<SpoilerNode<*>, Unit>
			):MessageRenderContext{
				MessageRenderContext(
					context,
					(adapter as WidgetChatListAdapter)
						.getData()
						.getUserId()
					,
					messageEntry.getAnimateEmojis(),
					messageEntry.getNickOrUsernames(),
					(adapter as WidgetChatListAdapter)
						.getData()
						.getChannelNames()
					,
					messageEntry.getRoles(),
					Utils.getResId("colorTextLink", "attr"),
					`WidgetChatListAdapterItemMessage$getMessageRenderContext$1`
						.INSTANCE
					,
					`WidgetChatListAdapterItemMessage$getMessageRenderContext$2`(
						this
					),
					ColorCompat.getThemedColor(
						context,
						Utils.getResId("theme_chat_spoiler_bg", "attr") as Int
					),
					ColorCompat.getThemedColor(
						context,
						Utils.getResId("theme_chat_spoiler_bg_visible", "attr") as Int
					),
					function1,
					`WidgetChatListAdapterItemMessage$getMessageRenderContext$3`(
						this
					),
					`WidgetChatListAdapterItemMessage$getMessageRenderContext$4`(
						context
					)
				);
			}
			fun getSpoilerClickHandler(
				message:Message
			):Function1<SpoilerNode<*>, Unit>{
				if(!(adapter as WidgetChatListAdapter).getData().isSpoilerClickAllowed()){
					return null;
				}
				`WidgetChatListAdapterItemMessage$getSpoilerClickHandler$1`(
					this, message
				);
			}
			fun processMessageText(
				simpleDraweeSpanTextView:SimpleDraweeSpanTextView,
				messageEntry:MessageEntry
			):Void?{
				var str = null as String?;
				var type = null as Int?;
				val context = simpleDraweeSpanTextView.getContext() as Context;
				val message = messageEntry.getMessage() as Message;
				val isWebhook = message.isWebhook() as Boolean;
				val  editedTimestamp = message.getEditedTimestamp() as UtcDateTime;
				var z2 = true as Boolean;
				var i = 0 as Int;
				var z3 = (if(editedTimestamp != null) editedTimestamp.g() else 0L) > 0 as Boolean;
				if(message.isSourceDeleted()){
					str = context
						.getResources()
						.getString(Utils.getResId("source_message_deleted", "string"))
					;
				}else{
					str = message.getContent();
					if (str == null) {
						str = "";
					}
				}
				m.checkNotNullExpressionValue(str, "if (message.isSourceDele…ssage.content ?: \"\"\n	}");
				val messagePreprocessor = getMessagePreprocessor(
					(adapter as WidgetChatListAdapter).getData().getUserId(),
					message,
					messageEntry.getMessageState()
				) as MessagePreprocessor;
				val parseChannelMessage = DiscordParser
					.parseChannelMessage(
						context,
						str,
						getMessageRenderContext(
							context,
							messageEntry,
							getSpoilerClickHandler(message)
						),
						messagePreprocessor,
						if(if(messageEntry.isGuildForumPostFirstMessage())
							DiscordParser.ParserOptions.FORUM_POST_FIRST_MESSAGE
							else isWebhook
						)
						DiscordParser.ParserOptions.ALLOW_MASKED_LINKS
						else DiscordParser.ParserOptions.DEFAULT,
						z3
					)
					as DraweeSpanStringBuilder
				;
				simpleDraweeSpanTextView.setAutoLinkMask(
					if(messagePreprocessor.isLinkifyConflicting() || !shouldLinkify(message.getContent())) 0 else 6
				);
				if(parseChannelMessage.length() <= 0){
					z2 = false;
				}
				if(!z2){
					i = 8;
				}
				simpleDraweeSpanTextView.setVisibility(i);
				simpleDraweeSpanTextView.setDraweeSpanStringBuilder(parseChannelMessage);
				val type2 = messageEntry.getMessage().getType() as Int;
				type = messageEntry.getMessage().getType();
				simpleDraweeSpanTextView.setAlpha(
					if(
						(type2 != null && type2.intValue() == -1)
					||
						(
							messageEntry.getMessage().getType() != null
						&&
							type.intValue() == -6
						)
					) 0.5f else 1.0f
				);
			}
			fun shouldLinkify(str:String?):boolean{
				if(str == null){
					return false;
				}
				if(str.length() < 200){
					return true;
				}
				val length = str.length() as Int;
				var i = 0 as Int;
				for(i2 in 0..length) {
					if(str.charAt(i2) == '.' && (i + 1) >= 50){
						return false;
					}
					i = i+1
				}
				return true;
			}
			fun shouldShowInteractionMessage(Message:message):boolean{
				return(
					message.isLocalApplicationCommand()
				||
					message.isLoading()
				);
			}
		}
	}
	override fun stop(context:Context) = patcher.unpatchAll();
}