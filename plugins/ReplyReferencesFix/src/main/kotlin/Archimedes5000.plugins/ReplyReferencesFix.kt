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
			return@balls null;
		}
	}
	override fun stop(pluginContext:Context) = patcher.unpatchAll();
}