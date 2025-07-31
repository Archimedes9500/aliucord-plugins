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
import android.widget.LinearLayout;

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
					is View -> View(context);
					is ViewGroup -> ViewGroup(context);
					is LinearLayout -> LinearLayout(context);
				}
			).apply{
				alpha = original.alpha;
				background = original.background;
				clickable = original.clickable;
				contentDescription = original.contentDescription;
				drawingCacheQuality = original.drawingCacheQuality;
				duplicateParentState = original.duplicateParentState;
				id = original.id;
				requiresFadingEdge = original.requiresFadingEdge;
				fadeScrollbars = original.fadeScrollbars;
				fadingEdgeLength = original.fadingEdgeLength;
				filterTouchesWhenObscured = original.filterTouchesWhenObscured;
				fitsSystemWindows = original.fitsSystemWindows;
				isScrollContainer = original.isScrollContainer;
				focusable = original.focusable;
				focusableInTouchMode = original.focusableInTouchMode;
				hapticFeedbackEnabled = original.hapticFeedbackEnabled;
				keepScreenOn = original.keepScreenOn;
				layerType = original.layerType;
				layoutDirection = original.layoutDirection;
				longClickable = original.longClickable;
				minHeight = original.minHeight;
				minWidth = original.minWidth;
				nextFocusDown = original.nextFocusDown;
				nextFocusLeft = original.nextFocusLeft;
				nextFocusRight = original.nextFocusRight;
				nextFocusUp = original.nextFocusUp;
				onClick = original.onClick;
				padding = original.padding;
				paddingBottom = original.paddingBottom;
				paddingLeft = original.paddingLeft;
				paddingRight = original.paddingRight;
				paddingTop = original.paddingTop;
				paddingStart = original.paddingStart;
				paddingEnd = original.paddingEnd;
				saveEnabled = original.saveEnabled;
				rotation = original.rotation;
				rotationX = original.rotationX;
				rotationY = original.rotationY;
				scaleX = original.scaleX;
				scaleY = original.scaleY;
				scrollX = original.scrollX;
				scrollY = original.scrollY;
				scrollbarSize = original.scrollbarSize;
				scrollbarStyle = original.scrollbarStyle;
				scrollbars = original.scrollbars;
				scrollbarDefaultDelayBeforeFade = original.scrollbarDefaultDelayBeforeFade;
				scrollbarFadeDuration = original.scrollbarFadeDuration;
				scrollbarTrackHorizontal = original.scrollbarTrackHorizontal;
				scrollbarThumbHorizontal = original.scrollbarThumbHorizontal;
				scrollbarThumbVertical = original.scrollbarThumbVertical;
				scrollbarTrackVertical = original.scrollbarTrackVertical;
				scrollbarAlwaysDrawHorizontalTrack = original.scrollbarAlwaysDrawHorizontalTrack;
				scrollbarAlwaysDrawVerticalTrack = original.scrollbarAlwaysDrawVerticalTrack;
				soundEffectsEnabled = original.soundEffectsEnabled;
				tag = original.tag;
				textAlignment = original.textAlignment;
				textDirection = original.textDirection;
				transformPivotX = original.transformPivotX;
				transformPivotY = original.transformPivotY;
				translationX = original.translationX;
				translationY = original.translationY;
				visibility = original.visibility;
		
				if(original is ViewGroup){
					clipChildren = original.clipChildren;
					clipToPadding = original.clipToPadding;
					layoutAnimation = original.layoutAnimation;
					animationCache = original.animationCache;
					persistentDrawingCache = original.persistentDrawingCache;
					alwaysDrawnWithCache = original.alwaysDrawnWithCache;
					addStatesFromChildren = original.addStatesFromChildren;
					descendantFocusability = original.descendantFocusability;
					animateLayoutChanges = original.animateLayoutChanges;
				}
		
				if(original is LinearLayout){
					baselineAligned = original.baselineAligned;
					baselineAlignedChildIndex = original.baselineAlignedChildIndex;
					gravity = original.gravity;
					measureWithLargestChild = original.measureWithLargestChild;
					orientation = original.orientation;
					weightSum = original.weightSum;
				}
			};
			return clone;
		}

		patcher.instead<WidgetChatListAdapterItemMessage>(
			"configureReplyPreview",
			MessageEntry::class.java
		)balls@{
			frame ->
			val messageEntry = frame.component1() as MessageEntry;
			//reflect
			val replyHolder = ReflectUtils.getField(this, "replyHolder") as View?;
			val replyLinkItem = ReflectUtils.getField(this, "replyLinkItem") as View?;
			val replyText = ReflectUtils.getField(this, "replyText") as SimpleDraweeSpanTextView?;
			val replyLeadingViewsHolder = ReflectUtils.getField(this, "replyLeadingViewsHolder") as View?;
			val adapter = ReflectUtils.getField(this, "adapter") as WidgetChatListAdapter?;
			fun configureReplyInteraction(messageEntry:MessageEntry){
				ReflectUtils.invokeMethod(
					this,
					"configureReplyInteraction",
					arrayOf(messageEntry)
				);
			}
			fun configureReplySystemMessage(resT:String, resS:String){
				ReflectUtils.invokeMethod(
					this,
					"configureReplySystemMessage",
					arrayOf(Utils.getResId(resT, resS))
				);
			}
			fun configureReplySystemMessageUserJoin(messageEntry:MessageEntry){
				ReflectUtils.invokeMethod(
					this,
					"configureReplySystemMessageUserJoin",
					arrayOf(messageEntry)
				);
			}
			fun configureReplyAuthor(coreUser:CoreUser, user:User, messageEntry:MessageEntry){
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
			fun configureReplyContentWithResourceId(resT:String, resS:String){
				ReflectUtils.invokeMethod(
					this,
					"configureReplyContentWithResourceId",
					arrayOf(Utils.getResId(resT, resS))
				);
			}
			///reflect
			val clone = clone(pluginContext, replyHolder!!);
			var type = null as Int?;
			if(replyHolder != null && replyLinkItem != null){
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
							configureReplyAuthor(CoreUser(author), author, messageEntry2);
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