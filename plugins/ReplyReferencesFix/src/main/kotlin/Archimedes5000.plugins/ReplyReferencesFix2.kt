package Archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.instead;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage;
import com.aliucord.patcher.component1;
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
//import androidx.core.view.ViewKt;
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
			"configureReplyPreview",
			MessageEntry::class.java
		){
			frame ->
			//reflection
			for(prop in this.memberProperties){
				prop.isAccessible = true;
				this.set(prop.name) = this.get(prop.name)
					as this.get(prop.name).returnType
				;
			}
			///reflection
			val messageEntry = frame.component1() as MessageEntry;
			val type = messageEntry.getMessage().getType() as Int?;
			if(
				this.replyHolder != null && this.replyLinkItem != null){
				val message = messageEntry.getMessage()
					as Message
				;
				var replyData = messageEntry.getReplyData()
					as MessageEntry.ReplyData?
				;
				val isInteraction = message.isInteraction()
					as Boolean
				;
				if(
					isInteraction
				||
					!(
						replyData == null
					||
						type == null
					||
						type != 19
					)
				){
					this.replyHolder.setVisibility(0);
					this.replyLinkItem.setVisibility(0);
					if(isInteraction){
						this.configureReplyInteraction(messageEntry);
					}else if(replyData != null){
						var messageEntry2 = replyData.getMessageEntry()
							as MessageEntry
						;
						var messageState = replyData.getMessageState()
							as StoreMessageReplies.MessageState
						;
						if(replyData.isRepliedUserBlocked()){
							this.configureReplySystemMessage(
								R.string.reply_quote_message_blocked
							);
						}else if(
							messageState is StoreMessageReplies.MessageState.Unloaded
						){
							this.configureReplySystemMessage(
								R.string.reply_quote_message_not_loaded
							);
						}else if(
							messageState is StoreMessageReplies.MessageState.Deleted
						){
							this.configureReplySystemMessage(
								R.string.reply_quote_message_deleted
							);
						}else if(
							(messageState is StoreMessageReplies.MessageState.Loaded)
						&&
							messageEntry2 != null
						){
							var message2 = messageEntry2.getMessage() as Message;
							this.replyHolder.setOnClickListener(
								`WidgetChatListAdapterItemMessage$configureReplyPreview$1`(
									message2
								)
							);
							var type2 = message2.getType() as Integer?;
							if(type2 != null && type2.intValue() == 7){
								this.configureReplySystemMessageUserJoin(messageEntry2);
								return;
							}
							var author = message2.getAuthor() as User;
							configureReplyAuthor(
								CoreUser(author),
								messageEntry2.getAuthor(),
								messageEntry2
							);
							if(
								this.replyText != null
							&&
								this.replyLeadingViewsHolder != null
							){
								var content = message2.getContent() as String?;
								if (content == null) {
									content = "";
								}
								if(!(content.length() == 0)){
									var context = this.replyText.getContext() as Context;
									var embeddedMessageParser = EmbeddedMessageParser
										.INSTANCE
										as EmbeddedMessageParser?
									;
									var parse = embeddedMessageParser
										.parse(
											EmbeddedMessageParser.ParserData(
												context,
												messageEntry2.getRoles(),
												messageEntry2.getNickOrUsernames(),
												messageEntry2.getAnimateEmojis(),
												StoreMessageState.State(null, null, 3, null),
												50,
												message2,
												this.adapter as WidgetChatListAdapter
											)
										)
										as DraweeSpanStringBuilder
									;
									parse.setSpan(
										getLeadingEdgeSpan(),
										0,
										parse.length(),
										33
									);
									this.replyText.setDraweeSpanStringBuilder(parse);
									this.configureReplyLayoutDirection();
								}else if(message2.hasStickers()){
									this.configureReplyContentWithResourceId(
										R.string.reply_quote_sticker_mobile
									);
								}else if(
									message2.hasAttachments()
								||
									message2.shouldShowReplyPreviewAsAttachment()
								||
									message2.hasEmbeds()
								){
									this.configureReplyContentWithResourceId(
										R.string.reply_quote_no_text_content_mobile
									);
								}else{
									var appLog = AppLog.g as AppLog?;
									Logger.`e$default`(
										appLog,
										"Unhandled reply preview: "
											+messageEntry2
										,
										null,
										null,
										6,
										null
									);
								}
							}
						}
					}
				}else{
					this.replyHolder.setVisibility(8);
					this.replyLinkItem.setVisibility(8);
				}
			}
		}
	}
	override fun stop(context:Context) = patcher.unpatchAll();
}