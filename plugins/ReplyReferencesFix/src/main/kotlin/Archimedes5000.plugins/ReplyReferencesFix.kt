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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.discord.models.user.User as UserModel;

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

		fun clone(context:Context, original:View):View{
			val clone = (
				when(original){
					is FrameLayout -> FrameLayout(context);
					else -> View(context);
				}
			).apply{
				alpha = original.alpha;
				background = original.background;
				isClickable = original.isClickable;
				contentDescription = original.contentDescription;
				drawingCacheQuality = original.drawingCacheQuality;
				isDuplicateParentStateEnabled = original.isDuplicateParentStateEnabled;
				id = original.id;
				isHorizontalFadingEdgeEnabled = original.isHorizontalFadingEdgeEnabled;
				isVerticalFadingEdgeEnabled = original.isVerticalFadingEdgeEnabled;
				isScrollbarFadingEnabled = original.isScrollbarFadingEnabled;
				setFadingEdgeLength(if(original.isHorizontalFadingEdgeEnabled) original.horizontalFadingEdgeLength else if(original.isVerticalFadingEdgeEnabled) original.verticalFadingEdgeLength else 0);
				filterTouchesWhenObscured = original.filterTouchesWhenObscured;
				fitsSystemWindows = original.fitsSystemWindows;
				isScrollContainer = original.isScrollContainer;
				focusable = original.focusable;
				isFocusableInTouchMode = original.isFocusableInTouchMode;
				isHapticFeedbackEnabled = original.isHapticFeedbackEnabled;
				keepScreenOn = original.keepScreenOn;
				setLayerType(original.layerType, null /*or use reflection ReflectUtils.getField(original, "mLayerPaint")*/);
				layoutDirection = original.layoutDirection;
				isLongClickable = original.isLongClickable;
				minimumHeight = original.minimumHeight;
				minimumWidth = original.minimumWidth;
				nextFocusDownId = original.nextFocusDownId;
				nextFocusLeftId = original.nextFocusLeftId;
				nextFocusRightId = original.nextFocusRightId;
				nextFocusUpId = original.nextFocusUpId;
				//onClick //impossible
				setPadding(original.paddingBottom, original.paddingLeft, original.paddingRight, original.paddingTop);
				setPaddingRelative(original.paddingStart, original.paddingTop, original.paddingEnd, original.paddingBottom);
				isSaveEnabled = original.isSaveEnabled;
				rotation = original.rotation;
				rotationX = original.rotationX;
				rotationY = original.rotationY;
				scaleX = original.scaleX;
				scaleY = original.scaleY;
				scrollX = original.scrollX;
				scrollY = original.scrollY;
				scrollBarSize = original.scrollBarSize;
				scrollBarStyle = original.scrollBarStyle;
				isHorizontalScrollBarEnabled = original.isHorizontalScrollBarEnabled;
				scrollBarDefaultDelayBeforeFade = original.scrollBarDefaultDelayBeforeFade;
				scrollBarFadeDuration = original.scrollBarFadeDuration;
				//horizontalScrollbarTrackDrawable = original.horizontalScrollbarTrackDrawable; //API 29
				//horizontalScrollbarThumbDrawable = original.horizontalScrollbarThumbDrawable; //API 29
				//verticalScrollbarThumbDrawable = original.verticalScrollbarThumbDrawable; //API 29
				//verticalScrollbarTrackDrawable = original.verticalScrollbarTrackDrawable; //API 29
				//scrollBarAlwaysDrawHorizontalTrack
				//scrollBarAlwaysDrawVerticalTrack
				isSoundEffectsEnabled = original.isSoundEffectsEnabled;
				tag = original.tag;
				textAlignment = original.textAlignment;
				textDirection = original.textDirection;
				pivotX = original.pivotX;
				pivotY = original.pivotY;
				translationX = original.translationX;
				translationY = original.translationY;
				visibility = original.visibility;
		
				if(original is ViewGroup){
					(this as ViewGroup).setClipChildren(original.clipChildren);
					(this as ViewGroup).setClipToPadding(original.clipToPadding);
					(this as ViewGroup).setLayoutAnimation(original.layoutAnimation);
					(this as ViewGroup).isAnimationCacheEnabled = original.isAnimationCacheEnabled;
					(this as ViewGroup).setPersistentDrawingCache(original.persistentDrawingCache);
					(this as ViewGroup).isAlwaysDrawnWithCacheEnabled = original.isAlwaysDrawnWithCacheEnabled;
					(this as ViewGroup).setAddStatesFromChildren(original.addStatesFromChildren());
					(this as ViewGroup).setDescendantFocusability(original.descendantFocusability);
					(this as ViewGroup).setLayoutTransition(original.getLayoutTransition());
				}
		
				if(original is FrameLayout){
					(this as FrameLayout).foregroundGravity = original.foregroundGravity;
					(this as FrameLayout).measureAllChildren = original.measureAllChildren;
				}
			};
			return clone;
		}

		patcher.instead<WidgetChatListAdapterItemMessage>(
			"configureReplyPreview",
			MessageEntry::class.java
		)balls@{
			frame ->
			val messageEntry = frame.args[0] as MessageEntry;
			//reflect
			val replyHolder = ReflectUtils.getField(this, "replyHolder") as View?;
			val replyLinkItem = ReflectUtils.getField(this, "replyLinkItem") as View?;
			val replyText = ReflectUtils.getField(this, "replyText") as SimpleDraweeSpanTextView?;
			val replyLeadingViewsHolder = ReflectUtils.getField(this, "replyLeadingViewsHolder") as View?;
			val adapter = WidgetChatListAdapterItemMessage
				.`access$getAdapter$p`(this)
				as WidgetChatListAdapter
			;
			fun WidgetChatListAdapterItemMessage.configureReplyInteraction(messageEntry:MessageEntry){
				ReflectUtils.invokeMethod(
					this,
					"configureReplyInteraction",
					arrayOf(messageEntry)
				);
			}
			fun WidgetChatListAdapterItemMessage.configureReplySystemMessage(resT:String, resS:String){
				ReflectUtils.invokeMethod(
					this,
					"configureReplySystemMessage",
					arrayOf(Utils.getResId(resT, resS))
				);
			}
			fun WidgetChatListAdapterItemMessage.configureReplySystemMessageUserJoin(messageEntry:MessageEntry){
				ReflectUtils.invokeMethod(
					this,
					"configureReplySystemMessageUserJoin",
					arrayOf(messageEntry)
				);
			}
			fun WidgetChatListAdapterItemMessage.configureReplyAuthor(user:UserModel, guildMember:GuildMember, messageEntry:MessageEntry){
				ReflectUtils.invokeMethod(
					this,
					"configureReplyAuthor",
					arrayOf(user, guildMember, messageEntry)
				);
			}
			fun WidgetChatListAdapterItemMessage.getLeadingEdgeSpan(){
				ReflectUtils.invokeMethod(
					this,
					"getLeadingEdgeSpan",
					arrayOf<Any>()
				);
			}
			fun WidgetChatListAdapterItemMessage.configureReplyLayoutDirection(){
				ReflectUtils.invokeMethod(
					this,
					"configureReplyLayoutDirection",
					arrayOf<Any>()
				);
			}
			fun WidgetChatListAdapterItemMessage.configureReplyContentWithResourceId(resT:String, resS:String){
				ReflectUtils.invokeMethod(
					this,
					"configureReplyContentWithResourceId",
					arrayOf(Utils.getResId(resT, resS))
				);
			}
			///reflect
			var type = null as Int?;
			if(replyHolder != null && replyLinkItem != null){
				val clone = clone(replyHolder.getContext(), replyHolder);
				(replyHolder.getParent() as ViewGroup).addView(clone);
				val message:Message = messageEntry.getMessage();
				val replyData:MessageEntry.ReplyData? = messageEntry.getReplyData();
				val isInteraction:Boolean = message.isInteraction();
				type = messageEntry.getMessage().getType();
				if(isInteraction||(replyData != null && type == REPLY)){
					//replyHolder.setVisibility(View.VISIBLE);
					//replyLinkItem.setVisibility(View.VISIBLE);
					if(isInteraction){
						configureReplyInteraction(messageEntry);
					}else if(replyData != null){
						val messageEntry2:MessageEntry? = replyData.getMessageEntry();
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
							/*replyHolder.setOnClickListener(
								`WidgetChatListAdapterItemMessage$configureReplyPreview$1`(
									message2
								)
							);*/
							val type2:Int? = message2.getType();
							if(type2 == USER_JOIN){
								configureReplySystemMessageUserJoin(messageEntry2);
								return@balls null;
							}
							val author:User = message2.getAuthor();
							val guildMember:GuildMember = messageEntry2.getAuthor();
							configureReplyAuthor(
								CoreUser(author) as UserModel,
								guildMember as GuildMember,
								messageEntry2 as MessageEntry
							);
							if(replyText != null && replyLeadingViewsHolder != null){
								var content:String? = message2.getContent();
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
					replyHolder?.setVisibility(View.GONE);
					replyLinkItem?.setVisibility(View.GONE);
				}
			}
			null;
		}
	}
	override fun stop(pluginContext:Context) = patcher.unpatchAll();
}