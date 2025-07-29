package Archimedes5000.plugins
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.instead;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapter;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage;
import com.discord.widgets.chat.list.adapter.`WidgetChatListAdapterItemMessage$configureReplyPreview$1`;
import com.aliucord.patcher.component1;
import com.aliucord.utils.ReflectUtils;
import com.aliucord.Utils;
import com.discord.models.domain.ModelMessageDelete;

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

@AliucordPlugin(requiresRestart = true)
class ReplyReferencesFix:Plugin(){
	@SuppressLint("SetTextI18n")
	override fun start(pluginContext:Context){
		patcher.instead<WidgetChatListAdapterItemMessage>(
			"configureReplyPreview",
			MessageEntry::class.java
		)balls@{
			frame ->
			val messageEntry = frame.component1() as MessageEntry;
			var type:Int? = null;
			if(
				(ReflectUtils.getField(this, "replyHolder") as View?) != null
			&&
				(ReflectUtils.getField(this, "replyLinkItem") as View?) != null
			){
				val message:Message = messageEntry.getMessage();
				val replyData:MessageEntry.ReplyData = messageEntry.getReplyData();
				val isInteraction:Boolean = message.isInteraction();
				type = messageEntry.getMessage().getType();
				if(
					isInteraction
				||
					!(
						replyData == null
					||
						messageEntry.getMessage().getType() == null
					||
						type != 19
					)
				){
					(ReflectUtils.getField(this, "replyHolder") as View?)?.setVisibility(0);
					(ReflectUtils.getField(this, "replyLinkItem") as View?)?.setVisibility(0);
					if(isInteraction){
						ReflectUtils.invokeMethod(
							this,
							"configureReplyInteraction",
							arrayOf(messageEntry)
						);
					}else if(replyData != null){
						val messageEntry2:MessageEntry = replyData.getMessageEntry();
						val messageState:StoreMessageReplies.MessageState =
							replyData
							.getMessageState()
						;
						if(replyData.isRepliedUserBlocked()){
							ReflectUtils.invokeMethod(
								this,
								"configureReplySystemMessage",
								arrayOf(
									Utils.getResId(
										"reply_quote_message_blocked",
										"string"
									)
								)
							);
							if(message.referencedMessage != null){
								val target = message.messageReference;
								(ReflectUtils.getField(this, "replyHolder") as View?)?.setOnClickListener{
									StoreStream
										.getMessagesLoader()
										.jumpToMessage(target.a(), target.c())
									;
									Utils.showToast("Blocked", showLonger = false);
								};
							}else{
							}
						}else if(
							messageState
								is
							StoreMessageReplies.MessageState.Unloaded
						){
							ReflectUtils.invokeMethod(
								this,
								"configureReplySystemMessage",
								arrayOf(
									Utils.getResId(
										"reply_quote_message_not_loaded",
										"string"
									)
								)
							);
							if(message.referencedMessage != null){
								val target = message.messageReference;
								(ReflectUtils.getField(this, "replyHolder") as View?)?.setOnClickListener{
									StoreStream
										.getMessagesLoader()
										.jumpToMessage(target.a(), target.c())
									;
									Utils.showToast("Unloaded", showLonger = false);
								};
							}else{
							}
						}else if(
							messageState
								is
							StoreMessageReplies.MessageState.Deleted
						){
							ReflectUtils.invokeMethod(
								this,
								"configureReplySystemMessage", arrayOf(
									Utils.getResId(
										"reply_quote_message_deleted",
										"string"
									)
								)
							);
							if(message.referencedMessage != null){
								val target = message.messageReference;
								(ReflectUtils.getField(this, "replyHolder") as View?)?.setOnClickListener{
									StoreStream
										.getMessagesLoader()
										.jumpToMessage(target.a(), target.c())
									;
									Utils.showToast("Deleted fake", showLonger = false);
								};
							}else if(message.messageReference != null){
								val target = message.messageReference;
								(ReflectUtils.getField(this, "replyHolder") as View?)?.setOnClickListener{
									StoreStream
										.getMessagesLoader()
										.jumpToMessage(target.a(), target.c())
									;
									Utils.showToast("Deleted fr", showLonger = false);
								};
							}else{
							}
						}else if(
							(
								messageState
									is
								StoreMessageReplies.MessageState.Loaded
							)
						&&
							message.referencedMessage != null
						){
							val message2:Message = Message(message.referencedMessage);
							val target = message.messageReference;
							(ReflectUtils.getField(this, "replyHolder") as View?)?.setOnClickListener{
								StoreStream
									.getMessagesLoader()
									.jumpToMessage(target.a(), target.c())
								;
							};
							val type2:Int = message2.getType();
							if(type2 != null && type2 == 7){
								ReflectUtils.invokeMethod(
									this,
									"configureReplySystemMessageUserJoin",
									arrayOf(messageEntry2)
								);
								return@balls null;
							}
							val author:User = message2.getAuthor();
							ReflectUtils.invokeMethod(
								this,
								"configureReplyAuthor",
								arrayOf(
									CoreUser(author),
									messageEntry2.getAuthor(),
									messageEntry2
								)
							);
							if(
								(ReflectUtils.getField(this, "replyText")
									as SimpleDraweeSpanTextView?
								)
									!=
								null
							&&
								(ReflectUtils.getField(this, "replyLeadingViewsHolder") as View?)
									!=
								null
							){
								var content:String = message2.getContent();
								if(content == null){
									content = "";
								}
								if(!(content.length == 0)){
									val context:Context? =
										(
											ReflectUtils.getField(this, "replyText")
												as
											SimpleDraweeSpanTextView?
										)
										?.getContext()
									;
									val embeddedMessageParser:EmbeddedMessageParser =
										EmbeddedMessageParser.INSTANCE
									;
									val parse:DraweeSpanStringBuilder =
										embeddedMessageParser
										.parse(
											EmbeddedMessageParser.ParserData(
												context,
												messageEntry2.getRoles(),
												messageEntry2.getNickOrUsernames(),
												messageEntry2.getAnimateEmojis(),
												StoreMessageState.State(
													null,
													null,
													3,
													null
												),
												50,
												message2,
												(ReflectUtils.getField(this, "adapter")
													as WidgetChatListAdapter?
												)
											)
										)
									;
									parse.setSpan(
										ReflectUtils.invokeMethod(
											this,
											"getLeadingEdgeSpan",
											arrayOf<Any>()
										),
										0,
										parse.length,
										33
									);
									(ReflectUtils.getField(this, "replyText")
										as SimpleDraweeSpanTextView?
									)
										?.setDraweeSpanStringBuilder(parse)
									;
									ReflectUtils.invokeMethod(
										this,
										"configureReplyLayoutDirection",
										arrayOf<Any>()
									);
								}else if(message2.hasStickers()) {
									ReflectUtils.invokeMethod(
										this,
										"configureReplyContentWithResourceId",
										arrayOf(
											Utils.getResId(
												"reply_quote_sticker_mobile",
												"string"
											)
										)
									);
								}else if(
									message2.hasAttachments()
								||
									message2.shouldShowReplyPreviewAsAttachment()
								||
									message2.hasEmbeds()
								){
									ReflectUtils.invokeMethod(
										this,
										"configureReplyContentWithResourceId",
										arrayOf(
											Utils.getResId(
												"reply_quote_no_text_content_mobile", 
												"string"
											)
										)
									);
								}else{
									val appLog:AppLog = AppLog.g;
									Logger.`e$default`(
										appLog,
										"Unhandled reply preview: "
											+
										messageEntry2,
										null,
										null,
										6,
										null
									);
								}
							}else{
							}
						}else{
						}
					}else{
					}
				}else{
					(ReflectUtils.getField(this, "replyHolder") as View?)?.setVisibility(8);
					(ReflectUtils.getField(this, "replyLinkItem") as View?)?.setVisibility(8);
				}
			}else{
			}
		}
	}
	override fun stop(pluginContext:Context) = patcher.unpatchAll();
}