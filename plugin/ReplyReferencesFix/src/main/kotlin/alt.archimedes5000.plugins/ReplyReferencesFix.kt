package alt.archimedes5000.plugins
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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.discord.models.user.User as UserModel;
import kotlin.reflect.jvm.*;
import kotlin.reflect.*;
import java.lang.reflect.InvocationTargetException;
import kotlin.Throwable;
import com.aliucord.Logger as Balls;

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
			val messageEntry = frame.args[0] as MessageEntry;
			val adapter = WidgetChatListAdapterItemMessage
				.`access$getAdapter$p`(this)
				as WidgetChatListAdapter
			;
			val c = WidgetChatListAdapterItemMessage::class.java;
			//reflect
			val replyHolder = ReflectUtils.getField(this, "replyHolder") as View?;
			val replyLinkItem = ReflectUtils.getField(this, "replyLinkItem") as View?;
			val replyText = ReflectUtils.getField(this, "replyText") as SimpleDraweeSpanTextView?;
			val replyLeadingViewsHolder = ReflectUtils.getField(this, "replyLeadingViewsHolder") as View?;

			val method1 = c
				.getDeclaredMethod(
					"configureReplyInteraction",
					MessageEntry::class.java
				)
				.apply{isAccessible = true}
			;
			fun WidgetChatListAdapterItemMessage
			.configureReplyInteraction(
				messageEntry:MessageEntry
			){
				method1.invoke(this, messageEntry);
			}
			val method2 = c
				.getDeclaredMethod(
					"configureReplySystemMessage",
					Int::class.java
				)
				.apply{isAccessible = true}
			;
			fun WidgetChatListAdapterItemMessage
			.configureReplySystemMessage(
				resT:String,
				resS:String
			){
				method2.invoke(this, Utils.getResId(resT, resS));
			}
			val method3 = c
				.getDeclaredMethod(
					"configureReplySystemMessageUserJoin",
					MessageEntry::class.java
				)
				.apply{isAccessible = true}
			;
			fun WidgetChatListAdapterItemMessage
			.configureReplySystemMessageUserJoin(
				messageEntry:MessageEntry
			){
				method3.invoke(this, messageEntry);
			}
			val method4 = c
				.getDeclaredMethod(
					"configureReplyAuthor",
					UserModel::class.java,
					GuildMember::class.java,
					MessageEntry::class.java
				)
				.apply{isAccessible = true}
			;
			fun WidgetChatListAdapterItemMessage
			.configureReplyAuthor(
				user:UserModel,
				guildMember:GuildMember?,
				messageEntry:MessageEntry
			){
				method4.invoke(this, user, guildMember, messageEntry);
			}
			val method5 = c
				.getDeclaredMethod(
					"getLeadingEdgeSpan"
				)
				.apply{isAccessible = true}
			;
			fun WidgetChatListAdapterItemMessage
			.getLeadingEdgeSpan(
			):LeadingMarginSpan{
				return (method5.invoke(this) as LeadingMarginSpan);
			}
			val method6 = c
				.getDeclaredMethod(
					"configureReplyLayoutDirection"
				)
				.apply{isAccessible = true}
			;
			fun WidgetChatListAdapterItemMessage
			.configureReplyLayoutDirection(
			){
				method6.invoke(this);
			}
			val method7 = c
				.getDeclaredMethod(
					"configureReplyContentWithResourceId",
					Int::class.java
				)
				.apply{isAccessible = true}
			;
			fun WidgetChatListAdapterItemMessage
			.configureReplyContentWithResourceId(
				resT:String,
				resS:String
			){
				try{
					method7.invoke(this, Utils.getResId(resT, resS));
				}catch(e:InvocationTargetException){
					throw e.cause ?: Throwable("idk");
				}
			}
			///reflect
			var type:Int? = null;
			if(replyHolder != null && replyLinkItem != null){
				val message:Message = messageEntry.message;
				val replyData:MessageEntry.ReplyData? = messageEntry.replyData;
				val isInteraction:Boolean = message.isInteraction();
				type = messageEntry.message.type;
				if(isInteraction||(replyData != null && type == REPLY)){
					replyHolder.visibility = View.VISIBLE;
					replyLinkItem.visibility = View.VISIBLE;
					if(isInteraction){
						configureReplyInteraction(messageEntry);
					}else if(replyData != null){
						val messageEntry2:MessageEntry? = replyData.messageEntry;
						val messageState:StoreMessageReplies.MessageState =
							replyData
							.messageState
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
							val message2:Message = messageEntry2.message;
							replyHolder.setOnClickListener(
								`WidgetChatListAdapterItemMessage$configureReplyPreview$1`(
									message2
								)
							);
							val type2:Int? = message2.type;
							if(type2 == USER_JOIN){
								configureReplySystemMessageUserJoin(messageEntry2);
								return@balls null;
							}
							val author:User = message2.author;
							val guildMember:GuildMember? = messageEntry2.author;
							configureReplyAuthor(
								CoreUser(author) as UserModel,
								guildMember as GuildMember?,
								messageEntry2 as MessageEntry
							);
							if(replyText != null && replyLeadingViewsHolder != null){
								var content:String? = message2.content;
								if(content == null){
									content = "";
								}
								if(!(content.length == 0)){
									val context:Context = replyText.context;
									val embeddedMessageParser:EmbeddedMessageParser =
										EmbeddedMessageParser
										.INSTANCE
									;
									val parse:DraweeSpanStringBuilder = embeddedMessageParser
										.parse(
											EmbeddedMessageParser.ParserData(
												context,
												messageEntry2.roles,
												messageEntry2.nickOrUsernames,
												messageEntry2.animateEmojis,
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
									replyText.setDraweeSpanStringBuilder(parse);
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
										"Unhandled reply preview: "+messageEntry2,
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
					replyHolder?.visibility = View.GONE;
					replyLinkItem?.visibility = View.GONE;
				}
			}
			null;
		}
	}
	override fun stop(pluginContext:Context) = patcher.unpatchAll();
}