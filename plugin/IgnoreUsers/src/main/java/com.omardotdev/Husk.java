package io.github.juby210.acplugins;

import com.discord.api.channel.Channel;
import com.discord.api.role.GuildRole;
import com.discord.api.user.User;
import com.discord.api.utcdatetime.UtcDateTime;
import com.discord.models.member.GuildMember;
import com.discord.models.message.Message;
import com.discord.utilities.embed.InviteEmbedModel;
import com.discord.widgets.botuikit.ComponentChatListState;
import com.discord.widgets.chat.list.entries.ChatListEntry;
import com.discord.widgets.chat.list.model.WidgetChatListModelMessages;
import d0.t.n;
import d0.t.t;
import d0.t.u;
import d0.z.d.m;
import d0.z.d.o;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.jvm.functions.Function14;

import com.discord.widgets.chat.list.model.WidgetChatListModelMessages$Companion$get$1;
import com.aliucord.api.PatcherAPI;
import com.aliucord.patcher.InsteadHook;
import com.aliucord.Logger;

public class Husk{
	Husk(IgnoreUsers instance, PatcherAPI patcher){
		try{patcher.patch(WidgetChatListModelMessages$Companion$get$1.class.getDeclaredMethod("invoke", WidgetChatListModelMessages.MessagesWithMetadata.class, Channel.class, Map.class, List.class, Map.class, Long.class, Map.class, Long.class, boolean.class, boolean.class, boolean.class, long.class, Map.class, InviteEmbedModel.class, boolean.class),
			new InsteadHook(frame -> {
				//this
				WidgetChatListModelMessages$Companion$get$1 _this = (WidgetChatListModelMessages$Companion$get$1) frame.thisObject;

				//args
				WidgetChatListModelMessages.MessagesWithMetadata messagesWithMetadata = (WidgetChatListModelMessages.MessagesWithMetadata) frame.args[0];
				Channel channel = (Channel) frame.args[1];
				Map<Long, Integer> map = (Map<Long, Integer>) frame.args[2];
				List<Long> list = (List<Long>) frame.args[3];
				Map<Long, GuildMember> map2 = (Map<Long, GuildMember>) frame.args[4];
				Long l = (Long) frame.args[5];
				Map<Long, GuildRole> map3 = (Map<Long, GuildRole>) frame.args[6];
				Long l2 = (Long) frame.args[7];
				boolean z2 = (boolean) frame.args[8];
				boolean z3 = (boolean) frame.args[9];
				boolean z4 = (boolean) frame.args[10];
				long j = (long) frame.args[11];
				Map<Long, ComponentChatListState.ComponentStoreState> map4 = (Map<Long, ComponentChatListState.ComponentStoreState>) frame.args[12];
				InviteEmbedModel inviteEmbedModel = (InviteEmbedModel) frame.args[13];
				boolean ass = (boolean) frame.args[14];

				(new Logger("Husk")).debug(map.toString()+"\n"+instance.ignoredUsers.toString());

				Object obj;
				boolean z5;
				boolean z6;
				Message message;
				boolean z7;
				String str;
				Message message2;
				WidgetChatListModelMessages.Companion companion;
				WidgetChatListModelMessages.Items items;
				WidgetChatListModelMessages$Companion$get$1 widgetChatListModelMessages$Companion$get$1 = _this;
				WidgetChatListModelMessages.MessagesWithMetadata messagesWithMetadata2 = messagesWithMetadata;
				Map<Long, Integer> map5 = map;
				m.checkNotNullParameter(messagesWithMetadata2, "messagesWithMetadata");
				m.checkNotNullParameter(map5, "blockedRelationships");
				m.checkNotNullParameter(list, "blockedExpanded");
				m.checkNotNullParameter(map3, "guildRoles");
				m.checkNotNullParameter(map4, "componentStoreState");
				m.checkNotNullParameter(inviteEmbedModel, "inviteEmbedModel");
				WidgetChatListModelMessages.Items items2 = new WidgetChatListModelMessages.Items(messagesWithMetadata.getMessages().size());
				long j2 = 0;
				Message message3 = null;
				Message message4 = null;
				int i = 0;
				int i2 = 0;
				boolean z8 = false;
				boolean z9 = false;
				for (Object obj2 : messagesWithMetadata.getMessages()) {
					i++;
					if (i < 0) {
						n.throwIndexOverflow();
					}
					Message message5 = (Message) obj2;
					WidgetChatListModelMessages.Companion companion2 = WidgetChatListModelMessages.Companion;
					UtcDateTime timestamp = message5.getTimestamp();
					if (WidgetChatListModelMessages.Companion.access$willAddTimestamp(companion2, timestamp != null ? timestamp.g() : 0L, j2)) {
						i2 = WidgetChatListModelMessages.Companion.access$addBlockedMessage(companion2, items2, message3, i2, z8);
					}
					long id2 = message5.getId();
					UtcDateTime timestamp2 = message5.getTimestamp();
					j2 = WidgetChatListModelMessages.Companion.access$tryAddTimestamp(companion2, items2, id2, timestamp2 != null ? timestamp2.g() : 0L, j2);
					boolean z10 = i == messagesWithMetadata.getMessages().size() - 1;
					Integer type = message5.getType();
					if (type != null && type.intValue() == 21) {
						User author = ((Message) WidgetChatListModelMessages.Companion.access$getThreadStarterMessageAndChannel(companion2, channel, widgetChatListModelMessages$Companion$get$1.$channel, message5, messagesWithMetadata2).getFirst()).getAuthor();
						z6 = map5.containsKey(author != null ? Long.valueOf(author.getId()) : null) || instance.ignoredUsers.contains(author != null ? Long.toString(author.getId()) : null);
					} else {
						User author2 = message5.getAuthor();
						z6 = map5.containsKey(author2 != null ? Long.valueOf(author2.getId()) : null) || instance.ignoredUsers.contains(author2 != null ? Long.toString(author2.getId()) : null);
					}
					if (!z6 || (i2 = i2 + 1) != 1) {
						z7 = z8;
						message = message3;
					} else {
						z7 = list.contains(Long.valueOf(message5.getId()));
						message = message5;
					}
					i2 = (!z6 || z10) ? WidgetChatListModelMessages.Companion.access$addBlockedMessage(companion2, items2, message, i2, z7) : i2;
					boolean z11 = (z6 || !z7) ? z7 : false;
					if (!z6 || z11) {
						boolean access$shouldConcatMessage = WidgetChatListModelMessages.Companion.access$shouldConcatMessage(companion2, items2, message5, message4);
						items2.setConcatCount(access$shouldConcatMessage ? items2.getConcatCount() + 1 : 0);
						Integer type2 = message5.getType();
						if (type2 != null && type2.intValue() == 21) {
							Channel channel2 = widgetChatListModelMessages$Companion$get$1.$channel;
							m.checkNotNullExpressionValue(map2, "guildMembers");
							message3 = message;
							message2 = message5;
							str = "newMessagesMarkerMessageId";
							companion = companion2;
							items2.addItems(companion2.getThreadStarterMessageItems(channel, channel2, map2, map3, map, message5, messagesWithMetadata, z2, z3, z4, j, true, map4, inviteEmbedModel));
							items = items2;
						} else {
							message3 = message;
							message2 = message5;
							str = "newMessagesMarkerMessageId";
							companion = companion2;
							Channel channel3 = widgetChatListModelMessages$Companion$get$1.$channel;
							m.checkNotNullExpressionValue(map2, "guildMembers");
							items = items2;
							items.addItems(WidgetChatListModelMessages.Companion.getMessageItems$default(companion, channel3, map2, map3, map, messagesWithMetadata.getMessageThreads().get(Long.valueOf(message2.getId())), messagesWithMetadata.getThreadCountsAndLatestMessages().get(Long.valueOf(message2.getId())), message2, messagesWithMetadata.getMessageState().get(Long.valueOf(message2.getId())), messagesWithMetadata.getMessageReplyState(), z11, access$shouldConcatMessage, l2, z2, z3, z4, j, true, map4, inviteEmbedModel, false, ass, 524288, null));
						}
					} else {
						message3 = message;
						items = items2;
						message2 = message5;
						str = "newMessagesMarkerMessageId";
						companion = companion2;
					}
					if (!z9) {
						m.checkNotNullExpressionValue(l, str);
						items2 = items;
						widgetChatListModelMessages$Companion$get$1 = _this;
						z9 = WidgetChatListModelMessages.Companion.access$tryAddNewMessagesSeparator(companion, items2, l.longValue(), z10, message2.getId(), widgetChatListModelMessages$Companion$get$1.$channel);
					} else {
						items2 = items;
						widgetChatListModelMessages$Companion$get$1 = _this;
					}
					messagesWithMetadata2 = messagesWithMetadata;
					map5 = map;
					z8 = z11;
					message4 = message2;
				}
				t.reverse(items2.getItems());
				List<ChatListEntry> items3 = items2.getItems();
				Message message6 = (Message) u.firstOrNull((List<? extends Object>) messagesWithMetadata.getMessages());
				long id3 = message6 != null ? message6.getId() : 0L;
				Message message7 = (Message) u.lastOrNull((List<? extends Object>) messagesWithMetadata.getMessages());
				long id4 = message7 != null ? message7.getId() : 0L;
				m.checkNotNullExpressionValue(l, "newMessagesMarkerMessageId");
				long longValue = l.longValue();
				Iterator<Message> it = messagesWithMetadata.getMessages().iterator();
				while (true) {
					if (!it.hasNext()) {
						obj = null;
						break;
					}
					obj = it.next();
					User author3 = ((Message) obj).getAuthor();
					if (author3 == null || author3.getId() != j) {
						z5 = false;
					} else {
						z5 = true;
					}
					if (z5) {
						break;
					}
				}
				Message message8 = (Message) obj;
				return new WidgetChatListModelMessages(items3, id3, id4, map2, longValue, message8 != null ? Long.valueOf(message8.getId()) : null);
			})
		);}catch(Exception e){};
	}
}