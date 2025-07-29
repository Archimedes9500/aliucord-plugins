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
import com.discord.api.message.MessageTypes.*;
import com.discord.stores.StoreMessageReplies.MessageState.*;

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
			val messageEntry:MessageEntry = frame.component1();
			var type:Int? = null as Int?;
			//reflect
			val replyHolder:View? = ReflectUtils.getField(this, "replyHolder");
			val replyLinkItem:View? = ReflectUtils.getField(this, "replyLinkItem");
			val replyText:SimpleDraweeSpanTextView? = ReflectUtils.getField(this, "replyText");
			val replyLeadingViewsHolder:View? = ReflectUtils.getField(this, "replyLeadingViewsHolder");
			val adapter:WidgetChatListAdapter? = ReflectUtils.getField(this, "adapter");
			fun configureReplyInteraction(messageEntry){
				ReflectUtils.invokeMethod(
					this,
					"configureReplyInteraction",
					arrayOf(messageEntry)
				);
			}
			fun configureReplySystemMessage(resT, resS){
				ReflectUtils.invokeMethod(
					this,
					"configureReplySystemMessage",
					arrayOf(Utils.getResId(resT, resS))
				);
			}
			fun configureReplySystemMessageUserJoin(messageEntry){
				ReflectUtils.invokeMethod(
					this,
					"configureReplySystemMessageUserJoin",
					arrayOf(messageEntry)
				);
			}
			fun configureReplyAuthor(coreUser, user, messageEntry){
				ReflectUtils.invokeMethod(
					this,
					"configureReplyAuthor",
					arrayOf(coreUser, user, messageEntry)
				);
			}
			fun getLeadingEdgeSpan(){
				ReflectUtils.invokeMethod(
					this,
					"getLeadingEdgeSpan",
					arrayOf<Any>()
				);
			}
			fun configureReplyLayoutDirection(){
				ReflectUtils.invokeMethod(
					this,
					"configureReplyLayoutDirection",
					arrayOf<Any>()
				);
			}
			fun configureReplyContentWithResourceId(resT, resS){
				ReflectUtils.invokeMethod(
					this,
					"configureReplyContentWithResourceId",
					arrayOf(Utils.getResId(resT, resS))
				);
			}
			///reflect
			if(replyHolder != null && replyLinkItem != null){
				val message:Message = messageEntry.getMessage();
				val replyData:MessageEntry.ReplyData = messageEntry.getReplyData();
				val isInteraction:Boolean = message.isInteraction();
				type = messageEntry.getMessage().getType();
				if(isInteraction||(replyData != null && type == REPLY)){
					replyHolder.setVisibility(View.VISIBLE);
					replyLinkItem.setVisibility(View.VISIBLE);
					if(isInteraction){
						configureReplyInteraction(messageEntry);
					}else if(replyData != null){
						val messageEntry2:MessageEntry = replyData.getMessageEntry();
						val messageState:StoreMessageReplies.MessageState =
							replyData
							.getMessageState()
						;
						if(replyData.isRepliedUserBlocked()){
							configureReplySystemMessage(
								"string", "reply_quote_message_blocked"
							);
						}else if(messageState is Unloaded){
							configureReplySystemMessage(
								"string", "reply_quote_message_not_loaded"
							);
						}else if(messageState is Deleted){
							configureReplySystemMessage(
								"string", "reply_quote_message_deleted"
							);
						}else if(messageState is Loaded && messageEntry2 != null){
							val message2:Message = messageEntry2.getMessage();
							replyHolder.setOnClickListener(
								`WidgetChatListAdapterItemMessage$configureReplyPreview$1`(
									message2
								)
							);
							val type2:Int? = message2.getType();
							if(type2 == USER_JOIN){
								configureReplySystemMessageUserJoin(messageEntry2);
								return@balls;
							}
							val author:User = message2.getAuthor();
							configureReplyAuthor(CoreUser(author), author, messageEntry2);
							if(replyText != null && replyLeadingViewsHolder != null){
								val content:String? = message2.getContent();
								if(content == null){
									content = "";
								}
								if(!(content.length == 0)){
									val context:Context = replyText.getContext();
									val embeddedMessageParser:EmbeddedMessageParser =
										EmbeddedMessageParser
										.INSTANCE
									;
									val parse:DraweeSpanStringBuilder = embeddedMessageParser
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
												adapter
											)
										)
									;
									parse.setSpan(getLeadingEdgeSpan(), 0, parse.length, 33);
									replyText?.setDraweeSpanStringBuilder(parse);
									configureReplyLayoutDirection();
								}else if(message2.hasStickers()){
									configureReplyContentWithResourceId(
										"string", "reply_quote_sticker_mobile"
									);
								}else if(
									message2.hasAttachments()
								||
									message2.shouldShowReplyPreviewAsAttachment()
								||
									message2.hasEmbeds()
								){
									configureReplyContentWithResourceId(
										"string", "reply_quote_no_text_content_mobile"
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
							}
						}
					}
				}else{
					replyHolder?.setVisibility(View.GONE);
					replyLinkItem?.setVisibility(View.GONE);
				}
			}
		}
	}
	override fun stop(pluginContext:Context) = patcher.unpatchAll();
}