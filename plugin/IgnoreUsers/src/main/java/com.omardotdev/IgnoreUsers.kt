/*
 * Omar (omardotdev)'s Aliucord Plugins
 * Copyright (C) 2025 Omar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
*/

package com.omardotdev.ignoreusers

import android.content.Context
import android.graphics.*
import android.os.Message
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import b.a.a.d.`a$f`
import com.aliucord.*
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.GatewayAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.aliucord.utils.DimenUtils.dp
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.chat.*
import com.discord.widgets.chat.input.*
import com.discord.widgets.chat.list.adapter.*
import com.discord.widgets.chat.list.entries.ChatListEntry
import com.discord.widgets.chat.list.entries.MessageEntry
import com.discord.widgets.chat.list.model.WidgetChatListModelMessages
import com.discord.widgets.chat.list.model.WidgetChatListModelMessages.*
import com.discord.widgets.chat.list.model.WidgetChatListModelMessages.Companion.`access$addBlockedMessage`
import com.lytefast.flexinput.R

data class Relationship(val id: String, val user_ignored: Boolean? = null)
data class Ready(val relationships: List<Relationship>)


@AliucordPlugin
class IgnoreUsers : Plugin() {
    val viewId = View.generateViewId()

    // Adds the function for ignoring and unignoring a user.
    private fun ignoreUser(user: Long, isAlreadyIgnored: Boolean) {
        Utils.threadPool.execute {
            try {
                // Sends a PUT request to Discord for ignoring, if the user isn't ignored, it'll unignore
                val req = if (!isAlreadyIgnored) Http.Request.newDiscordRNRequest("/users/@me/relationships/$user/ignore", "PUT")
                            else Http.Request.newDiscordRNRequest("/users/@me/relationships/$user/ignore", "DELETE")
                req.execute()
                // Sets the string for the toast, and shows it
                val string = if (!isAlreadyIgnored) "User has been ignored :3" else "User has been unignored :3"
                Utils.mainThread.post {
                    Utils.showToast(string)
                }
            } catch (e: Exception) {
                Logger("IgnoreUsers").warn("Failed to ignore user", e)
            }
        }
    }

    override fun start(c: Context) {
        // Ignored users list
        var ignoredUsers = listOf<String>()

        // Get the ignored users list when GatewayAPI is ready
        GatewayAPI.onEvent<Ready>("READY") {
            ignoredUsers = it.relationships.filter { it.user_ignored ?: false }.map { it.id }
            Logger("IgnoreUsers").info("Ignored user :3 $ignoredUsers")

            // Patches the user menu so it includes ignoring
            patcher.after<`a$f`>("invoke", Any::class.java) { (_, avar: b.a.a.d.f.d.a) ->
                val view = (receiver as b.a.a.d.a).requireView() as LinearLayout
                val layout = view
                val ctx = layout.context
                val baseIndex = layout.indexOfChild(view.getChildAt(1) as AppCompatTextView)
                // Adds the buttom, if it already exists, don't add the button so it doesn't multiply :3
                layout.findViewById(viewId) ?: TextView(
                    ctx,
                    null,
                    0,
                    R.i.UiKit_ListItem_Icon
                ).apply {
                    if (ignoredUsers.contains(avar.a.id.toString())) text = "Unignore" else text = "Ignore" // Sets the text
                    setCompoundDrawablesWithIntrinsicBounds(
                        Utils.tintToTheme(ctx.getDrawable(com.lytefast.flexinput.R.e.design_ic_visibility)),
                        null,
                        null,
                        null
                    ) // Sets the icon
                    id = viewId // Sets the view ID
                    setOnClickListener {
                        if (ignoredUsers.contains(avar.a.id.toString())) {
                            ignoreUser(avar.a.id, true)
                            ignoredUsers =
                                ignoredUsers - avar.a.id.toString() // Removes ignored user from list
                        } else {
                            ignoreUser(avar.a.id, false)
                            ignoredUsers =
                                ignoredUsers + avar.a.id.toString() // Adds ignored user to list
                        }
                    }
                    layout.addView(this, baseIndex + 1) // Adds view
                }
            }


             // Adds the ignored message indicator
             Husk(patcher)

        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}
