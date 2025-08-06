package Archimedes5000.plugins;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher;
import com.aliucord.patcher.Hook;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapter;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage$configureReplyPreview$1;
import com.aliucord.utils.ReflectUtils;
import com.aliucord.Utils;
import com.discord.api.message.MessageTypes.*;
import com.discord.stores.StoreMessageReplies.MessageState.*;
import android.view.ViewGroup;
import android.widget.FrameLayout;
//import com.discord.models.user.User;
import java.lang.reflect.InvocationTargetException;
//import com.aliucord.Logger;
import java.lang.reflect.Method;

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

@AliucordPlugin(requiresRestart = false)
public class ReplyReferencesFix extends Plugin {
	@SuppressLint("SetTextI18n")
	//reflect
	public static class Reflect{
		public WidgetChatListAdapterItemMessage instance;
		public Class<WidgetChatListAdapterItemMessage> c;
		public Method method1;
		public Method method2;
		public Method method3;
		public Method method4;
		public Method method5;
		public Method method6;
		public Method method7;
		public Reflect(WidgetChatListAdapterItemMessage instance){
			this.instance = instance;
			this.c = WidgetChatListAdapterItemMessage.class;
			this.method1 = this.c.getDeclaredMethod(
				"configureReplyInteraction",
				MessageEntry.class
			);
			this.method1.setAccessible(true);
			this.method2 = this.c.getDeclaredMethod(
				"configureReplySystemMessage",
				Integer.class
			);
			this.method2.setAccessible(true);
			this.method3 = this.c.getDeclaredMethod(
				"configureReplySystemMessageUserJoin",
				MessageEntry.class
			);
			this.method3.setAccessible(true);
			this.method4 = this.c.getDeclaredMethod(
				"configureReplyAuthor",
				com.discord.models.user.User.class,
				GuildMember.class,
				MessageEntry.class
			);
			this.method4.setAccessible(true);
			this.method5 = this.c.getDeclaredMethod(
				"getLeadingEdgeSpan"
			);
			this.method5.setAccessible(true);
			this.method6 = this.c.getDeclaredMethod(
				"configureReplyLayoutDirection"
			);
			this.method6.setAccessible(true);
			this.method7 = this.c.getDeclaredMethod(
				"configureReplyContentWithResourceId",
				Integer.class
			);
			this.method7.setAccessible(true);
		}
		public void configureReplyInteraction(
			MessageEntry messageEntry
		){
			method1.invoke(instance, messageEntry);
		}
		public void configureReplySystemMessage(
			String resT,
			String resS
		){
			method2.invoke(instance, Utils.getResId(resT, resS));
		}
		public void configureReplySystemMessageUserJoin(
			MessageEntry messageEntry
		){
			method3.invoke(instance, messageEntry);
		}
		public void configureReplyAuthor(
			com.discord.models.user.User user,
			GuildMember guildMember,
			MessageEntry messageEntry
		){
			method4.invoke(instance, user, guildMember, messageEntry);
		}
		public LeadingMarginSpan getLeadingEdgeSpan(){
			method5.invoke(instance);
		}
		public void configureReplyLayoutDirection(){
			method6.invoke(instance);
		}
		public void configureReplyContentWithResourceId(
			String resT,
			String resS
		){
			method7.invoke(instance, Utils.getResId(resT, resS));
		}
	}
	///reflect
    @Override
	public void start(Context pluginContext) throws Throwable {
		patcher.patch(
			WidgetChatListAdapterItemMessage.class
			.getDeclaredMethod(
				"configureReplyPreview",
				MessageEntry.class
			),
			new Hook(frame -> {
				MessageEntry messageEntry = (MessageEntry)frame.args[0];
				Reflect reflect = new Reflect((WidgetChatListAdapterItemMessage)frame.thisObject);
				WidgetChatListAdapter adapter = (WidgetChatListAdapter)WidgetChatListAdapterItemMessage
					.access$getAdapter$p((WidgetChatListAdapterItemMessage)frame.thisObject)
				;
				//reflect
				var replyHolder = (View)ReflectUtils.getField(frame.thisObject, "replyHolder");
				var replyLinkItem = (View)ReflectUtils.getField(frame.thisObject, "replyLinkItem");
				var replyText = (SimpleDraweeSpanTextView)ReflectUtils.getField(frame.thisObject, "replyText");
				var replyLeadingViewsHolder = (View)ReflectUtils.getField(frame.thisObject, "replyLeadingViewsHolder");
				///reflect
				Integer type;
				if (replyHolder != null && replyLinkItem != null) {
				Message message = messageEntry.getMessage();
					MessageEntry.ReplyData replyData = messageEntry.getReplyData();
					boolean isInteraction = message.isInteraction();
					if (isInteraction || !(replyData == null || (type = messageEntry.getMessage().getType()) == null || type.intValue() != 19)) {
						replyHolder.setVisibility(0);
						replyLinkItem.setVisibility(0);
						if (isInteraction) {
							reflect.configureReplyInteraction(messageEntry);
						} else if (replyData != null) {
							MessageEntry messageEntry2 = replyData.getMessageEntry();
							StoreMessageReplies.MessageState messageState = replyData.getMessageState();
							if (replyData.isRepliedUserBlocked()) {
								reflect.configureReplySystemMessage("reply_quote_message_blocked", "string");
							} else if (messageState instanceof StoreMessageReplies.MessageState.Unloaded) {
								reflect.configureReplySystemMessage("reply_quote_message_not_loaded", "string");
							} else if (messageState instanceof StoreMessageReplies.MessageState.Deleted) {
								reflect.configureReplySystemMessage("reply_quote_message_deleted", "string");
							} else if ((messageState instanceof StoreMessageReplies.MessageState.Loaded) && messageEntry2 != null) {
								Message message2 = messageEntry2.getMessage();
								replyHolder.setOnClickListener(new WidgetChatListAdapterItemMessage$configureReplyPreview$1(message2));
								Integer type2 = message2.getType();
								if (type2 != null && type2.intValue() == 7) {
									reflect.configureReplySystemMessageUserJoin(messageEntry2);
									return;
								}
								User author = message2.getAuthor();
								m.checkNotNull(author);
								reflect.configureReplyAuthor(new CoreUser(author), messageEntry2.getAuthor(), messageEntry2);
								if (replyText != null && replyLeadingViewsHolder != null) {
									String content = message2.getContent();
									if (content == null) {
										content = "";
									}
									if (!(content.length() == 0)) {
										Context context = replyText.getContext();
										EmbeddedMessageParser embeddedMessageParser = EmbeddedMessageParser.INSTANCE;
										m.checkNotNullExpressionValue(context, "context");
										DraweeSpanStringBuilder parse = embeddedMessageParser.parse(new EmbeddedMessageParser.ParserData(context, messageEntry2.getRoles(), messageEntry2.getNickOrUsernames(), messageEntry2.getAnimateEmojis(), new StoreMessageState.State(null, null, 3, null), 50, message2, (WidgetChatListAdapter) adapter));
										parse.setSpan(reflect.getLeadingEdgeSpan(), 0, parse.length(), 33);
										replyText.setDraweeSpanStringBuilder(parse);
										reflect.configureReplyLayoutDirection();
									} else if (message2.hasStickers()) {
										reflect.configureReplyContentWithResourceId("reply_quote_sticker_mobile", "string");
									} else if (message2.hasAttachments() || message2.shouldShowReplyPreviewAsAttachment() || message2.hasEmbeds()) {
										reflect.configureReplyContentWithResourceId("reply_quote_no_text_content_mobile", "string");
									} else {
										AppLog appLog = AppLog.g;
										Logger.e$default(appLog, "Unhandled reply preview: " + messageEntry2, null, null, 6, null);
									}
								}
							}
						}
					} else {
						replyHolder.setVisibility(8);
						replyLinkItem.setVisibility(8);
					}
				}
			})
		);
	}
	@Override
	public void stop(Context pluginContext) {patcher.unpatchAll();};
}