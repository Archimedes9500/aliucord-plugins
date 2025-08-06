package Archimedes5000.plugins;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.patch;
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
    @Override
	public void start(Context pluginContext) throws Throwable {
		patcher.patch(
			WidgetChatListAdapterItemMessage.class
			.getDeclaredMethod(
				"configureReplyPreview",
				MessageEntry.class
			),
			new Hook(frame -> {
				MessageEntry messageEntry = frame.args[0];
				Integer type;
				if (this.replyHolder != null && this.replyLinkItem != null) {
				Message message = messageEntry.getMessage();
					MessageEntry.ReplyData replyData = messageEntry.getReplyData();
					boolean isInteraction = message.isInteraction();
					if (isInteraction || !(replyData == null || (type = messageEntry.getMessage().getType()) == null || type.intValue() != 19)) {
						this.replyHolder.setVisibility(0);
						this.replyLinkItem.setVisibility(0);
						if (isInteraction) {
							configureReplyInteraction(messageEntry);
						} else if (replyData != null) {
							MessageEntry messageEntry2 = replyData.getMessageEntry();
							StoreMessageReplies.MessageState messageState = replyData.getMessageState();
							if (replyData.isRepliedUserBlocked()) {
								configureReplySystemMessage(R.string.reply_quote_message_blocked);
							} else if (messageState instanceof StoreMessageReplies.MessageState.Unloaded) {
								configureReplySystemMessage(R.string.reply_quote_message_not_loaded);
							} else if (messageState instanceof StoreMessageReplies.MessageState.Deleted) {
								configureReplySystemMessage(R.string.reply_quote_message_deleted);
							} else if ((messageState instanceof StoreMessageReplies.MessageState.Loaded) && messageEntry2 != null) {
								Message message2 = messageEntry2.getMessage();
								this.replyHolder.setOnClickListener(new WidgetChatListAdapterItemMessage$configureReplyPreview$1(message2));
								Integer type2 = message2.getType();
								if (type2 != null && type2.intValue() == 7) {
									configureReplySystemMessageUserJoin(messageEntry2);
									return;
								}
								User author = message2.getAuthor();
								m.checkNotNull(author);
								configureReplyAuthor(new CoreUser(author), messageEntry2.getAuthor(), messageEntry2);
								if (this.replyText != null && this.replyLeadingViewsHolder != null) {
									String content = message2.getContent();
									if (content == null) {
										content = "";
									}
									if (!(content.length() == 0)) {
										Context context = this.replyText.getContext();
										EmbeddedMessageParser embeddedMessageParser = EmbeddedMessageParser.INSTANCE;
										m.checkNotNullExpressionValue(context, "context");
										DraweeSpanStringBuilder parse = embeddedMessageParser.parse(new EmbeddedMessageParser.ParserData(context, messageEntry2.getRoles(), messageEntry2.getNickOrUsernames(), messageEntry2.getAnimateEmojis(), new StoreMessageState.State(null, null, 3, null), 50, message2, (WidgetChatListAdapter) this.adapter));
										parse.setSpan(getLeadingEdgeSpan(), 0, parse.length(), 33);
										this.replyText.setDraweeSpanStringBuilder(parse);
										configureReplyLayoutDirection();
									} else if (message2.hasStickers()) {
										configureReplyContentWithResourceId(R.string.reply_quote_sticker_mobile);
									} else if (message2.hasAttachments() || message2.shouldShowReplyPreviewAsAttachment() || message2.hasEmbeds()) {
										configureReplyContentWithResourceId(R.string.reply_quote_no_text_content_mobile);
									} else {
										AppLog appLog = AppLog.g;
										Logger.e$default(appLog, "Unhandled reply preview: " + messageEntry2, null, null, 6, null);
									}
								}
							}
						}
					} else {
						this.replyHolder.setVisibility(8);
						this.replyLinkItem.setVisibility(8);
					}
				}
			})
		);
	}
	@Override
	public void stop(Context pluginContext) {patcher.unpatchAll();};
}