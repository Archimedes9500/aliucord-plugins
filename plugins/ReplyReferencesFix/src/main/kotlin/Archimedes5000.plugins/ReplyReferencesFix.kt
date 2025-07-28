package Archimedes5000.plugins;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.instead;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage;
import com.aliucord.patcher.component1;
import com.aliucord.patcher.component2;
import com.aliucord.utils.ReflectUtils;

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
import androidx.core.view.ViewKt;
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
			@LayoutRes Int::class.java,
			WidgetChatListAdapter::class.java
		){
			frame ->
			val i = frame.component1() as Int;
			val widgetChatListAdapter = frame.component2() as WidgetChatListAdapter;
			super(i, widgetChatListAdapter);
			val findViewById = this.itemView
				.findViewById(R.id.chat_list_adapter_item_text)
				as View
			;
			this.itemText = findViewById as SimpleDraweeSpanTextView;
			this.MAX_REPLY_AST_NODES = 50 as Int;
			this.itemAvatar = this.itemView.findViewById(R.id.chat_list_adapter_item_text_avatar) as ImageView;
			this.itemName = this.itemView.findViewById(R.id.chat_list_adapter_item_text_name) as TextView;
			this.itemRoleIcon = this.itemView.findViewById(R.id.chat_list_adapter_item_text_role_icon) as RoleIconView;
			this.itemTag = this.itemView.findViewById(R.id.chat_list_adapter_item_text_tag) as TextView;
			this.replyHolder = this.itemView.findViewById(R.id.chat_list_adapter_item_text_decorator) as View;
			this.replyLinkItem = this.itemView.findViewById(R.id.chat_list_adapter_item_text_decorator_reply_link_icon) as View;
			this.replyLeadingViewsHolder = this.itemView.findViewById(R.id.chat_list_adapter_item_reply_leading_views) as View;
			this.replyName = this.itemView.findViewById(R.id.chat_list_adapter_item_text_decorator_reply_name) as TextView;
			this.replyIcon = this.itemView.findViewById(R.id.chat_list_adapter_item_text_decorator_reply_icon) as ImageView;
			this.replyAvatar = this.itemView.findViewById(R.id.chat_list_adapter_item_text_decorator_avatar) as ImageView;
			this.replyText = this.itemView.findViewById(R.id.chat_list_adapter_item_text_reply_content) as SimpleDraweeSpanTextView;
			this.itemTimestamp = this.itemView.findViewById(R.id.chat_list_adapter_item_text_timestamp) as TextView;
			this.failedUploadList = this.itemView.findViewById(R.id.chat_list_adapter_item_failed_upload_list) as FailedUploadList;
			this.itemAlertText = this.itemView.findViewById(R.id.chat_list_adapter_item_alert_text) as TextView;
			this.itemLoadingText = this.itemView.findViewById(R.id.chat_list_adapter_item_text_loading) as TextView;
			this.backgroundHighlight = this.itemView.findViewById(R.id.chat_list_adapter_item_highlighted_bg) as View;
			this.gutterHighlight = this.itemView.findViewById(R.id.chat_list_adapter_item_gutter_bg) as View;
			this.loadingDots = this.itemView.findViewById(R.id.chat_overlay_typing_dots) as TypingDots;
			this.sendError = this.itemView.findViewById(R.id.chat_list_adapter_item_text_error) as ImageView;
			this.threadEmbedSpine = this.itemView.findViewById(R.id.chat_list_adapter_item_thread_embed_spine) as ImageView;
			this.threadStarterMessageHeader = this.itemView.findViewById(R.id.thread_starter_message_header) as View;
			this.communicationDisabledIcon = this.itemView.findViewById(R.id.chat_list_adapter_item_communication_disabled_icon) as ImageView;
	
			fun configureThreadSpine(Message:message, Boolean:z2):void{
				val imageView = this.threadEmbedSpine as ImageView?;
				if(imageView != null){
					ViewKt.setVisible(imageView, message.hasThread() && !z2);
				}
			}
			fun getAuthorTextColor(GuildMember:guildMember):int{
				val view = this.itemView as View;
				GuildMember.Companion.getColor(
					guildMember,
					ColorCompat.getThemedColor(
						view.getContext(),
						R.attr.colorHeaderPrimary as int
					)
				);
			}
			fun getLeadingEdgeSpan():LeadingMarginSpan{
				var i = null as int;
				val view = this.replyLeadingViewsHolder as View;
				if(view != null) {
					view.measure(0, 0);
					i = this.replyLeadingViewsHolder.getMeasuredWidth();
				}else{
					i = 0;
				}
				LeadingMarginSpan.Standard(i, 0);
			}
			fun getMessagePreprocessor(long:j, Message:message, (StoreMessageState.State):state):MessagePreprocessor{
				val userSettings = StoreStream
					.Companion
					.getUserSettings()
					as StoreUserSettings
				;
				MessagePreprocessor(
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
					Context:context,
					MessageEntry:messageEntry,
					Function1<? super SpoilerNode<?>, Unit>:function1
			):MessageRenderContext{
				MessageRenderContext(
					context,
					(this.adapter as WidgetChatListAdapter)
						.getData()
						.getUserId()
					,
					messageEntry.getAnimateEmojis(),
					messageEntry.getNickOrUsernames(),
					(this.adapter as WidgetChatListAdapter)
						.getData()
						.getChannelNames()
					,
					messageEntry.getRoles(),
					R.attr.colorTextLink,
					WidgetChatListAdapterItemMessage$getMessageRenderContext$1
						.INSTANCE
					,
					WidgetChatListAdapterItemMessage$getMessageRenderContext$2(
						this
					),
					ColorCompat.getThemedColor(
						context,
						R.attr.theme_chat_spoiler_bg as int
					),
					ColorCompat.getThemedColor(
						context,
						R.attr.theme_chat_spoiler_bg_visible as int
					),
					function1,
					WidgetChatListAdapterItemMessage$getMessageRenderContext$3(
						this
					),
					WidgetChatListAdapterItemMessage$getMessageRenderContext$4(
						context
					)
				);
			}
			fun getSpoilerClickHandler(
				Message:message
			):Function1<SpoilerNode<?>, Unit>{
				if(!(this.adapter as WidgetChatListAdapter).getData().isSpoilerClickAllowed()){
					return null;
				}
				WidgetChatListAdapterItemMessage$getSpoilerClickHandler$1(
					this, message
				);
			}
			fun processMessageText(
				SimpleDraweeSpanTextView:simpleDraweeSpanTextView,
				MessageEntry:messageEntry
			):void{
				var str = null as String?;
				var type = null as Int?;
				val context = simpleDraweeSpanTextView.getContext() as Context;
				val message = messageEntry.getMessage() as Message;
				val isWebhook = message.isWebhook() as Boolean;
				val  editedTimestamp = message.getEditedTimestamp() as UtcDateTime;
				var z2 = true as Boolean;
				var i = 0 as Int;
				var z3 = (editedTimestamp != null ? editedTimestamp.g() : 0L) > 0 as Boolean;
				if(message.isSourceDeleted()){
					str = context
						.getResources()
						.getString(R.string.source_message_deleted)
					;
				}else{
					str = message.getContent();
					if (str == null) {
						str = "";
					}
				}
				m.checkNotNullExpressionValue(str, "if (message.isSourceDele…ssage.content ?: \"\"\n	}");
				val messagePreprocessor = getMessagePreprocessor(
					(this.adapter as WidgetChatListAdapter).getData().getUserId(),
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
				simpleDraweeSpanTextView.setAutoLinkMask((messagePreprocessor.isLinkifyConflicting() || !shouldLinkify(message.getContent())) ? 0 : 6);
				if(parseChannelMessage.length() <= 0){
					z2 = false;
				}
				if(!z2){
					i = 8;
				}
				simpleDraweeSpanTextView.setVisibility(i);
				simpleDraweeSpanTextView.setDraweeSpanStringBuilder(parseChannelMessage);
				val type2 = messageEntry.getMessage().getType() as Int;
				simpleDraweeSpanTextView.setAlpha(
					(
						(type2 != null && type2.intValue() == -1)
					||
					(
						(type = messageEntry.getMessage().getType()) != null
					&&
						type.intValue() == -6)
					) ? 0.5f : 1.0f
				);
			}
			fun shouldLinkify(String?:str):boolean{
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