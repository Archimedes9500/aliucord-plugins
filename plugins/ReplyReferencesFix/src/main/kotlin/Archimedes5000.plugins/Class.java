public final class WidgetChatListAdapterItemMessage extends WidgetChatListItem {
	public static final Companion Companion = new Companion(null);
	private static final int MAX_REPLY_AST_NODES = 50;
	private final SimpleDraweeSpanTextView itemText;
	private final ImageView itemAvatar = (ImageView) this.itemView.findViewById(R.id.chat_list_adapter_item_text_avatar);
	private final TextView itemName = (TextView) this.itemView.findViewById(R.id.chat_list_adapter_item_text_name);
	private final RoleIconView itemRoleIcon = (RoleIconView) this.itemView.findViewById(R.id.chat_list_adapter_item_text_role_icon);
	private final TextView itemTag = (TextView) this.itemView.findViewById(R.id.chat_list_adapter_item_text_tag);
	private final View replyHolder = this.itemView.findViewById(R.id.chat_list_adapter_item_text_decorator);
	private final View replyLinkItem = this.itemView.findViewById(R.id.chat_list_adapter_item_text_decorator_reply_link_icon);
	private final View replyLeadingViewsHolder = this.itemView.findViewById(R.id.chat_list_adapter_item_reply_leading_views);
	private final TextView replyName = (TextView) this.itemView.findViewById(R.id.chat_list_adapter_item_text_decorator_reply_name);
	private final ImageView replyIcon = (ImageView) this.itemView.findViewById(R.id.chat_list_adapter_item_text_decorator_reply_icon);
	private final ImageView replyAvatar = (ImageView) this.itemView.findViewById(R.id.chat_list_adapter_item_text_decorator_avatar);
	private final SimpleDraweeSpanTextView replyText = (SimpleDraweeSpanTextView) this.itemView.findViewById(R.id.chat_list_adapter_item_text_reply_content);
	private final TextView itemTimestamp = (TextView) this.itemView.findViewById(R.id.chat_list_adapter_item_text_timestamp);
	private final FailedUploadList failedUploadList = (FailedUploadList) this.itemView.findViewById(R.id.chat_list_adapter_item_failed_upload_list);
	private final TextView itemAlertText = (TextView) this.itemView.findViewById(R.id.chat_list_adapter_item_alert_text);
	private final TextView itemLoadingText = (TextView) this.itemView.findViewById(R.id.chat_list_adapter_item_text_loading);
	private final View backgroundHighlight = this.itemView.findViewById(R.id.chat_list_adapter_item_highlighted_bg);
	private final View gutterHighlight = this.itemView.findViewById(R.id.chat_list_adapter_item_gutter_bg);
	private final TypingDots loadingDots = (TypingDots) this.itemView.findViewById(R.id.chat_overlay_typing_dots);
	private final ImageView sendError = (ImageView) this.itemView.findViewById(R.id.chat_list_adapter_item_text_error);
	private final ImageView threadEmbedSpine = (ImageView) this.itemView.findViewById(R.id.chat_list_adapter_item_thread_embed_spine);
	private final View threadStarterMessageHeader = this.itemView.findViewById(R.id.thread_starter_message_header);
	private final ImageView communicationDisabledIcon = (ImageView) this.itemView.findViewById(R.id.chat_list_adapter_item_communication_disabled_icon);

	/* compiled from: WidgetChatListAdapterItemMessage.kt */
	/* loaded from: classes2.dex */
	public static final class Companion {
		private Companion() {
		}

		public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
			this();
		}
	}

	/* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
	public WidgetChatListAdapterItemMessage(@LayoutRes int i, WidgetChatListAdapter widgetChatListAdapter) {
		super(i, widgetChatListAdapter);
		m.checkNotNullParameter(widgetChatListAdapter, "adapter");
		View findViewById = this.itemView.findViewById(R.id.chat_list_adapter_item_text);
		m.checkNotNullExpressionValue(findViewById, "itemView.findViewById(R.…t_list_adapter_item_text)");
		this.itemText = (SimpleDraweeSpanTextView) findViewById;
	}

	public static final /* synthetic */ WidgetChatListAdapter access$getAdapter$p(WidgetChatListAdapterItemMessage widgetChatListAdapterItemMessage) {
		return (WidgetChatListAdapter) widgetChatListAdapterItemMessage.adapter;
	}

	public static final /* synthetic */ SimpleDraweeSpanTextView access$getItemText$p(WidgetChatListAdapterItemMessage widgetChatListAdapterItemMessage) {
		return widgetChatListAdapterItemMessage.itemText;
	}

	public static final /* synthetic */ SimpleDraweeSpanTextView access$getReplyText$p(WidgetChatListAdapterItemMessage widgetChatListAdapterItemMessage) {
		return widgetChatListAdapterItemMessage.replyText;
	}

	private final void configureCommunicationDisabled(GuildMember guildMember, Long l) {
		boolean z2 = false;
		boolean isCommunicationDisabled = guildMember != null ? guildMember.isCommunicationDisabled() : false;
		boolean z3 = PermissionUtils.can(Permission.MODERATE_MEMBERS, l) || PermissionUtils.can(8L, l);
		if (isCommunicationDisabled && z3) {
			z2 = true;
		}
		ImageView imageView = this.communicationDisabledIcon;
		if (imageView != null) {
			ViewKt.setVisible(imageView, z2);
		}
		ImageView imageView2 = this.itemAvatar;
		if (imageView2 != null) {
			imageView2.setAlpha(z2 ? 0.5f : 1.0f);
		}
	}

	private final TextView configureInteractionMessage(MessageEntry messageEntry) {
		int i;
		Message message = messageEntry.getMessage();
		boolean shouldShowInteractionMessage = shouldShowInteractionMessage(message);
		TypingDots typingDots = this.loadingDots;
		if (typingDots != null) {
			ViewKt.setVisible(typingDots, shouldShowInteractionMessage);
		}
		if (shouldShowInteractionMessage) {
			TypingDots typingDots2 = this.loadingDots;
			if (typingDots2 != null) {
				int i2 = TypingDots.j;
				typingDots2.a(false);
			}
		} else {
			TypingDots typingDots3 = this.loadingDots;
			if (typingDots3 != null) {
				typingDots3.b();
			}
		}
		TextView textView = this.itemLoadingText;
		if (textView != null) {
			ViewKt.setVisible(textView, shouldShowInteractionMessage);
		}
		this.itemText.setVisibility(shouldShowInteractionMessage ^ true ? 0 : 8);
		ImageView imageView = this.sendError;
		if (imageView != null) {
			ViewKt.setVisible(imageView, false);
		}
		if (!shouldShowInteractionMessage) {
			return this.itemText;
		}
		TextView textView2 = this.itemTag;
		if (textView2 != null) {
			ViewKt.setVisible(textView2, true);
		}
		if (message.isFailed()) {
			View view = this.itemView;
			m.checkNotNullExpressionValue(view, "itemView");
			i = ColorCompat.getThemedColor(view, (int) R.attr.colorError);
		} else {
			View view2 = this.itemView;
			m.checkNotNullExpressionValue(view2, "itemView");
			i = ColorCompat.getThemedColor(view2, (int) R.attr.colorTextMuted);
		}
		TextView textView3 = this.itemLoadingText;
		if (textView3 != null) {
			textView3.setTextColor(i);
		}
		if (message.isLocalApplicationCommand() && message.isLoading()) {
			TextView textView4 = this.itemLoadingText;
			if (textView4 != null) {
				Context context = this.itemText.getContext();
				m.checkNotNullExpressionValue(context, "itemText.context");
				Object[] objArr = new Object[1];
				Map<Long, String> nickOrUsernames = messageEntry.getNickOrUsernames();
				User author = message.getAuthor();
				objArr[0] = nickOrUsernames.get(author != null ? Long.valueOf(author.getId()) : null);
				textView4.setText(b.h(context, R.string.application_command_waiting, objArr, null, 4));
			}
			ImageView imageView2 = this.sendError;
			if (imageView2 != null) {
				ViewKt.setVisible(imageView2, false);
			}
		} else if (message.isLocalApplicationCommand() && message.isFailed()) {
			TextView textView5 = this.itemLoadingText;
			if (textView5 != null) {
				textView5.setText(R.string.application_command_failed);
			}
			ImageView imageView3 = this.sendError;
			if (imageView3 != null) {
				ViewKt.setVisible(imageView3, true);
			}
			TypingDots typingDots4 = this.loadingDots;
			if (typingDots4 != null) {
				ViewKt.setInvisible(typingDots4, true);
			}
			TypingDots typingDots5 = this.loadingDots;
			if (typingDots5 != null) {
				typingDots5.b();
			}
		} else if (message.isLocalApplicationCommand()) {
			TextView textView6 = this.itemLoadingText;
			if (textView6 != null) {
				textView6.setText(R.string.application_command_sending);
			}
			ImageView imageView4 = this.sendError;
			if (imageView4 != null) {
				ViewKt.setVisible(imageView4, false);
			}
		}
		TextView textView7 = this.itemLoadingText;
		return textView7 != null ? textView7 : this.itemText;
	}

	private final void configureItemTag(Message message, boolean z2) {
		if (this.itemTag != null) {
			User author = message.getAuthor();
			m.checkNotNull(author);
			CoreUser coreUser = new CoreUser(author);
			boolean isPublicGuildSystemMessage = PublicGuildUtils.INSTANCE.isPublicGuildSystemMessage(message);
			Integer type = message.getType();
			boolean z3 = true;
			boolean z4 = (type == null || type.intValue() != 0 || message.getMessageReference() == null) ? false : true;
			TextView textView = this.itemTag;
			if (!coreUser.isBot() && !coreUser.isSystemUser() && !isPublicGuildSystemMessage && !z4 && !z2) {
				z3 = false;
			}
			textView.setVisibility(z3 ? 0 : 8);
			this.itemTag.setText((coreUser.isSystemUser() || isPublicGuildSystemMessage) ? R.string.system_dm_tag_system : z4 ? R.string.bot_tag_server : z2 ? R.string.bot_tag_forum_original_poster : R.string.bot_tag_bot);
			this.itemTag.setCompoundDrawablesWithIntrinsicBounds(UserUtils.INSTANCE.isVerifiedBot(coreUser) ? R.drawable.ic_verified_10dp : 0, 0, 0, 0);
		}
	}

	private final void configureReplyAuthor(com.discord.models.user.User user, GuildMember guildMember, MessageEntry messageEntry) {
		configureReplyAvatar(user, guildMember != null ? guildMember : messageEntry.getAuthor());
		String str = (String) a.e(user, messageEntry.getNickOrUsernames());
		if (str == null) {
			str = user.getUsername();
		}
		boolean z2 = false;
		List<User> mentions = messageEntry.getMessage().getMentions();
		if (mentions != null) {
			for (User user2 : mentions) {
				if (user2.getId() == user.getId()) {
					z2 = true;
				}
			}
		}
		configureReplyName(str, getAuthorTextColor(guildMember), z2);
	}

	private final void configureReplyAvatar(com.discord.models.user.User user, GuildMember guildMember) {
		ImageView imageView = this.replyIcon;
		if (imageView != null && this.replyAvatar != null) {
			if (user == null) {
				imageView.setVisibility(0);
				this.replyAvatar.setVisibility(8);
				return;
			}
			imageView.setVisibility(8);
			this.replyAvatar.setVisibility(0);
			IconUtils.setIcon$default(this.replyAvatar, user, R.dimen.avatar_size_reply, null, null, guildMember, 24, null);
		}
	}

	private final void configureReplyContentWithResourceId(int i) {
		if (this.replyText != null) {
			Context context = this.replyText.getContext();
			m.checkNotNullExpressionValue(context, "replyText.context");
			SpannableString spannableString = new SpannableString(context.getResources().getString(i));
			spannableString.setSpan(new StyleSpan(2), 0, spannableString.length(), 33);
			configureReplyText(spannableString, 0.64f);
		}
	}

	private final void configureReplyInteraction(MessageEntry messageEntry) {
		User c;
		Message message = messageEntry.getMessage();
		Interaction interaction = message.getInteraction();
		if (interaction != null && (c = interaction.c()) != null) {
			GuildMember interactionAuthor = messageEntry.getInteractionAuthor();
			CoreUser coreUser = new CoreUser(c);
			configureReplyAvatar(new CoreUser(c), messageEntry.getAuthor());
			configureReplyAuthor(coreUser, interactionAuthor, messageEntry);
			TextView textView = this.replyName;
			if (textView != null) {
				textView.setOnClickListener(new WidgetChatListAdapterItemMessage$configureReplyInteraction$1(this, message));
			}
			SimpleDraweeSpanTextView simpleDraweeSpanTextView = this.replyText;
			if (simpleDraweeSpanTextView != null) {
				MovementMethod instance = LinkMovementMethod.getInstance();
				if (instance != null) {
					simpleDraweeSpanTextView.setMovementMethod(instance);
				} else {
					return;
				}
			}
			SimpleDraweeSpanTextView simpleDraweeSpanTextView2 = this.replyText;
			CharSequence d = simpleDraweeSpanTextView2 != null ? b.d(simpleDraweeSpanTextView2, R.string.system_message_application_command_reply, new Object[]{interaction.b()}, new WidgetChatListAdapterItemMessage$configureReplyInteraction$content$1(this, interaction, message, c)) : null;
			if (d != null) {
				SpannableString valueOf = SpannableString.valueOf(d);
				m.checkNotNullExpressionValue(valueOf, "valueOf(this)");
				configureReplyText(valueOf, 1.0f);
			}
		}
	}

	private final void configureReplyLayoutDirection() {
		if (this.replyHolder != null && this.replyText != null) {
			this.replyHolder.setLayoutDirection(BidiFormatter.getInstance().isRtl(this.replyText.getText()) ? 1 : 0);
		}
	}

	@SuppressLint({"SetTextI18n"})
	private final void configureReplyName(String str, int i, boolean z2) {
		if (this.replyName != null) {
			if (!(str == null || str.length() == 0)) {
				String str2 = z2 ? "@" : "";
				this.replyName.setVisibility(0);
				TextView textView = this.replyName;
				textView.setText(str2 + str);
				this.replyName.setTextColor(i);
				return;
			}
			this.replyName.setVisibility(8);
		}
	}

	private final void configureReplyPreview(MessageEntry messageEntry) {
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
	}

	private final void configureReplySystemMessage(int i) {
		configureReplyAvatar(null, null);
		configureReplyName("", 0, false);
		configureReplyContentWithResourceId(i);
	}

	private final void configureReplySystemMessageUserJoin(MessageEntry messageEntry) {
		ImageView imageView = this.replyIcon;
		if (imageView != null && this.replyAvatar != null && this.replyText != null) {
			imageView.setVisibility(8);
			this.replyAvatar.setVisibility(0);
			this.replyAvatar.setImageResource(R.drawable.ic_group_join);
			configureReplyName("", 0, false);
			Context context = this.replyText.getContext();
			Map<Long, String> nickOrUsernames = messageEntry.getNickOrUsernames();
			User author = messageEntry.getMessage().getAuthor();
			Long valueOf = author != null ? Long.valueOf(author.getId()) : null;
			m.checkNotNullExpressionValue(context, "context");
			configureReplyText(new SpannableString(b.h(context, MessageUtils.INSTANCE.getSystemMessageUserJoin(context, messageEntry.getMessage().getId()), new Object[]{nickOrUsernames.get(valueOf)}, null, 4).toString()), 0.64f);
		}
	}

	private final void configureReplyText(Spannable spannable, float f) {
		if (this.replyText != null && this.replyLeadingViewsHolder != null) {
			spannable.setSpan(getLeadingEdgeSpan(), 0, spannable.length(), 33);
			this.replyText.setAlpha(f);
			this.replyText.setText(spannable);
			configureReplyLayoutDirection();
		}
	}

	public static /* synthetic */ void configureReplyText$default(WidgetChatListAdapterItemMessage widgetChatListAdapterItemMessage, Spannable spannable, float f, int i, Object obj) {
		if ((i & 2) != 0) {
			f = 1.0f;
		}
		widgetChatListAdapterItemMessage.configureReplyText(spannable, f);
	}

	private final void configureThreadSpine(Message message, boolean z2) {
		ImageView imageView = this.threadEmbedSpine;
		if (imageView != null) {
			ViewKt.setVisible(imageView, message.hasThread() && !z2);
		}
	}

	private final int getAuthorTextColor(GuildMember guildMember) {
		View view = this.itemView;
		m.checkNotNullExpressionValue(view, "itemView");
		return GuildMember.Companion.getColor(guildMember, ColorCompat.getThemedColor(view.getContext(), (int) R.attr.colorHeaderPrimary));
	}

	private final LeadingMarginSpan getLeadingEdgeSpan() {
		int i;
		View view = this.replyLeadingViewsHolder;
		if (view != null) {
			view.measure(0, 0);
			i = this.replyLeadingViewsHolder.getMeasuredWidth();
		} else {
			i = 0;
		}
		return new LeadingMarginSpan.Standard(i, 0);
	}

	private final MessagePreprocessor getMessagePreprocessor(long j, Message message, StoreMessageState.State state) {
		StoreUserSettings userSettings = StoreStream.Companion.getUserSettings();
		return new MessagePreprocessor(j, state, (!userSettings.getIsEmbedMediaInlined() || !userSettings.getIsRenderEmbedsEnabled()) ? null : message.getEmbeds(), true, (Integer) null);
	}

	private final MessageRenderContext getMessageRenderContext(Context context, MessageEntry messageEntry, Function1<? super SpoilerNode<?>, Unit> function1) {
		return new MessageRenderContext(context, ((WidgetChatListAdapter) this.adapter).getData().getUserId(), messageEntry.getAnimateEmojis(), messageEntry.getNickOrUsernames(), ((WidgetChatListAdapter) this.adapter).getData().getChannelNames(), messageEntry.getRoles(), R.attr.colorTextLink, WidgetChatListAdapterItemMessage$getMessageRenderContext$1.INSTANCE, new WidgetChatListAdapterItemMessage$getMessageRenderContext$2(this), ColorCompat.getThemedColor(context, (int) R.attr.theme_chat_spoiler_bg), ColorCompat.getThemedColor(context, (int) R.attr.theme_chat_spoiler_bg_visible), function1, new WidgetChatListAdapterItemMessage$getMessageRenderContext$3(this), new WidgetChatListAdapterItemMessage$getMessageRenderContext$4(context));
	}

	private final Function1<SpoilerNode<?>, Unit> getSpoilerClickHandler(Message message) {
		if (!((WidgetChatListAdapter) this.adapter).getData().isSpoilerClickAllowed()) {
			return null;
		}
		return new WidgetChatListAdapterItemMessage$getSpoilerClickHandler$1(this, message);
	}

	private final void processMessageText(SimpleDraweeSpanTextView simpleDraweeSpanTextView, MessageEntry messageEntry) {
		String str;
		Integer type;
		Context context = simpleDraweeSpanTextView.getContext();
		Message message = messageEntry.getMessage();
		boolean isWebhook = message.isWebhook();
		UtcDateTime editedTimestamp = message.getEditedTimestamp();
		boolean z2 = true;
		int i = 0;
		boolean z3 = (editedTimestamp != null ? editedTimestamp.g() : 0L) > 0;
		if (message.isSourceDeleted()) {
			m.checkNotNullExpressionValue(context, "context");
			str = context.getResources().getString(R.string.source_message_deleted);
		} else {
			str = message.getContent();
			if (str == null) {
				str = "";
			}
		}
		m.checkNotNullExpressionValue(str, "if (message.isSourceDele…ssage.content ?: \"\"\n	}");
		MessagePreprocessor messagePreprocessor = getMessagePreprocessor(((WidgetChatListAdapter) this.adapter).getData().getUserId(), message, messageEntry.getMessageState());
		m.checkNotNullExpressionValue(context, "context");
		DraweeSpanStringBuilder parseChannelMessage = DiscordParser.parseChannelMessage(context, str, getMessageRenderContext(context, messageEntry, getSpoilerClickHandler(message)), messagePreprocessor, messageEntry.isGuildForumPostFirstMessage() ? DiscordParser.ParserOptions.FORUM_POST_FIRST_MESSAGE : isWebhook ? DiscordParser.ParserOptions.ALLOW_MASKED_LINKS : DiscordParser.ParserOptions.DEFAULT, z3);
		simpleDraweeSpanTextView.setAutoLinkMask((messagePreprocessor.isLinkifyConflicting() || !shouldLinkify(message.getContent())) ? 0 : 6);
		if (parseChannelMessage.length() <= 0) {
			z2 = false;
		}
		if (!z2) {
			i = 8;
		}
		simpleDraweeSpanTextView.setVisibility(i);
		simpleDraweeSpanTextView.setDraweeSpanStringBuilder(parseChannelMessage);
		Integer type2 = messageEntry.getMessage().getType();
		simpleDraweeSpanTextView.setAlpha(((type2 != null && type2.intValue() == -1) || ((type = messageEntry.getMessage().getType()) != null && type.intValue() == -6)) ? 0.5f : 1.0f);
	}

	private final boolean shouldLinkify(String str) {
		if (str == null) {
			return false;
		}
		if (str.length() < 200) {
			return true;
		}
		int length = str.length();
		int i = 0;
		for (int i2 = 0; i2 < length; i2++) {
			if (str.charAt(i2) == '.' && (i = i + 1) >= 50) {
				return false;
			}
		}
		return true;
	}

	private final boolean shouldShowInteractionMessage(Message message) {
		return message.isLocalApplicationCommand() || message.isLoading();
	}

	/* JADX WARN: Can't rename method to resolve collision */
	@Override // com.discord.widgets.chat.list.adapter.WidgetChatListItem
	public void onConfigure(int i, ChatListEntry chatListEntry) {
		TextView textView;
		List<Long> list;
		NullSerializable<String> a;
		View view;
		m.checkNotNullParameter(chatListEntry, "data");
		super.onConfigure(i, chatListEntry);
		MessageEntry messageEntry = (MessageEntry) chatListEntry;
		long j = 0;
		if (((WidgetChatListAdapter) this.adapter).getData().getUserId() != 0) {
			Message message = messageEntry.getMessage();
			boolean isThreadStarterMessage = messageEntry.isThreadStarterMessage();
			configureItemTag(message, messageEntry.isGuildForumPostAuthor());
			View view2 = this.backgroundHighlight;
			if (!(view2 == null || (view = this.gutterHighlight) == null)) {
				configureCellHighlight(message, view2, view);
			}
			TextView textView2 = this.itemName;
			Long l = null;
			if (textView2 != null) {
				Map<Long, String> nickOrUsernames = messageEntry.getNickOrUsernames();
				User author = message.getAuthor();
				textView2.setText(nickOrUsernames.get(author != null ? Long.valueOf(author.getId()) : null));
				this.itemName.setTextColor(getAuthorTextColor(messageEntry.getAuthor()));
				this.itemName.setOnClickListener(new WidgetChatListAdapterItemMessage$onConfigure$1(this, message));
				ViewExtensions.setOnLongClickListenerConsumeClick(this.itemName, new WidgetChatListAdapterItemMessage$onConfigure$2(this, message));
			}
			TextView textView3 = this.itemTimestamp;
			if (textView3 != null) {
				Context x2 = a.x(this.itemView, "itemView", "itemView.context");
				UtcDateTime timestamp = message.getTimestamp();
				if (timestamp != null) {
					j = timestamp.g();
				}
				textView3.setText(TimeUtils.toReadableTimeString$default(x2, j, null, 4, null));
			}
			configureInteractionMessage(messageEntry);
			if (!shouldShowInteractionMessage(message) || (textView = this.itemLoadingText) == null) {
				processMessageText(this.itemText, messageEntry);
				textView = this.itemText;
			}
			ViewCompat.setAccessibilityDelegate(this.itemView, new ChatListItemMessageAccessibilityDelegate(textView, this.itemName, this.itemTag, this.itemTimestamp));
			View view3 = this.threadStarterMessageHeader;
			if (view3 != null) {
				ViewKt.setVisible(view3, isThreadStarterMessage);
			}
			this.itemView.setOnClickListener(new WidgetChatListAdapterItemMessage$onConfigure$3(this, message, isThreadStarterMessage));
			View view4 = this.itemView;
			m.checkNotNullExpressionValue(view4, "itemView");
			ViewExtensions.setOnLongClickListenerConsumeClick(view4, new WidgetChatListAdapterItemMessage$onConfigure$4(this, message, isThreadStarterMessage));
			configureReplyPreview(messageEntry);
			ImageView imageView = this.itemAvatar;
			boolean z2 = true;
			if (imageView != null) {
				imageView.setOnClickListener(new WidgetChatListAdapterItemMessage$onConfigure$5(this, message));
				ViewExtensions.setOnLongClickListenerConsumeClick(this.itemAvatar, new WidgetChatListAdapterItemMessage$onConfigure$6(this, message));
				User author2 = message.getAuthor();
				String a2 = (author2 == null || (a = author2.a()) == null) ? null : a.a();
				if (message.getApplicationId() != null) {
					User author3 = message.getAuthor();
					if ((!m.areEqual(author3 != null ? author3.e() : null, Boolean.TRUE)) && a2 != null) {
						IconUtils.setIcon$default(this.itemAvatar, IconUtils.getApplicationIcon$default(message.getApplicationId().longValue(), a2, 0, 4, (Object) null), 0, (Function1) null, (MGImages.ChangeDetector) null, 28, (Object) null);
					}
				}
				ImageView imageView2 = this.itemAvatar;
				User author4 = message.getAuthor();
				m.checkNotNull(author4);
				IconUtils.setIcon$default(imageView2, new CoreUser(author4), R.dimen.avatar_size_chat, null, null, messageEntry.getAuthor(), 24, null);
			}
			if (this.itemRoleIcon != null) {
				StoreStream.Companion companion = StoreStream.Companion;
				Channel channel = companion.getChannels().getChannel(messageEntry.getMessage().getChannelId());
				GuildMember.Companion companion2 = GuildMember.Companion;
				GuildMember author5 = messageEntry.getAuthor();
				if (author5 == null || (list = author5.getRoles()) == null) {
					list = n.emptyList();
				}
				GuildRole highestRoleIconRole = companion2.getHighestRoleIconRole(list, companion.getGuilds().getRoles().get(channel != null ? Long.valueOf(channel.i()) : null));
				RoleIconView roleIconView = this.itemRoleIcon;
				if (channel != null) {
					l = Long.valueOf(channel.i());
				}
				roleIconView.setRole(highestRoleIconRole, l);
			}
			if (this.failedUploadList != null) {
				List<LocalAttachment> localAttachments = message.getLocalAttachments();
				if (!message.isFailed() || localAttachments == null || !(!localAttachments.isEmpty())) {
					this.failedUploadList.setVisibility(8);
				} else {
					this.failedUploadList.setVisibility(0);
					this.failedUploadList.setUp(localAttachments);
				}
			}
			if (this.itemAlertText != null) {
				if (message.isFailed()) {
					this.itemAlertText.setVisibility(0);
					Integer type = message.getType();
					if (type == null || type.intValue() != -3) {
						z2 = false;
					}
					this.itemAlertText.setText(z2 ? R.string.invalid_attachments_failure : R.string.send_message_failure);
				} else {
					this.itemAlertText.setVisibility(8);
				}
			}
			configureThreadSpine(message, isThreadStarterMessage);
			configureCommunicationDisabled(messageEntry.getAuthor(), messageEntry.getPermissionsForChannel());
		}
	}
}