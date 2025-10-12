package com.omardotdev.ignoreusers

import androidx.core.content.res.ResourcesCompat
import android.content.Context
import android.graphics.*
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView

import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.aliucord.api.GatewayAPI
import com.aliucord.utils.DimenUtils.dp

import com.discord.utilities.color.ColorCompat
import com.discord.widgets.chat.*
import com.discord.widgets.chat.input.*
import com.discord.widgets.chat.list.adapter.*
import com.discord.widgets.chat.list.entries.ChatListEntry
import com.discord.widgets.chat.list.entries.MessageEntry
import b.a.a.d.a as UserActionsDialog
import b.a.a.d.f as UserActionsDialogViewModel5

import com.lytefast.flexinput.R

data class Relationship(val id: String, val user_ignored: Boolean? = null)
data class Ready(val relationships: List<Relationship>)

@AliucordPlugin
class IgnoreUsers : Plugin() {
    private fun ignoreUser(userId: Long, isAlreadyIgnored: Boolean) {
        Utils.threadPool.execute {
            try {
                Http.Request.newDiscordRNRequest("/users/@me/relationships/$userId/ignore", if (isAlreadyIgnored) "DELETE" else "PUT").execute()
                Utils.showToast(if (isAlreadyIgnored) "User has been unignored :3" else "User has been ignored :3")
            } catch (e: Exception) {
                logger.error("Failed to ignore user", e)
            }
        }
    }

    override fun start(ctx: Context) {
        val viewId = View.generateViewId()
        var ignoredUsers = mutableListOf<Long>()

        GatewayAPI.onEvent<Ready>("READY") {
            ignoredUsers = it.relationships.filter { it.user_ignored ?: false }.map { it.id.toLong() }
            logger.info("Ignored users: $ignoredUsers")
        }

        patcher.after<UserActionsDialog.f>("invoke", UserActionsDialogViewModel5.d.a::class.java) { (_, aVar: UserActionsDialogViewModel5.d.a) ->
            val layout = (receiver as UserActionsDialog).getView() as LinearLayout

            if (layout.findViewById<TextView>(viewId) == null) {
                val textView = TextView(layout.context, null, 0, R.i.UiKit_ListItem_Icon).apply {
                    id = viewId
                    text = if (ignoredUsers.contains(aVar.a.id)) "Unignore" else "Ignore"
                    setCompoundDrawablesWithIntrinsicBounds(
                        Utils.tintToTheme(ctx.getDrawable(R.e.design_ic_visibility)),
                        null,
                        null,
                        null
                    )

                    setOnClickListener {
                        if (ignoredUsers.contains(aVar.a.id)) {
                            ignoreUser(aVar.a.id, true)
                            ignoredUsers.remove(aVar.a.id)
                        } else {
                            ignoreUser(aVar.a.id, false)
                            ignoredUsers.add(aVar.a.id)
                        }
                    }
                }

                val baseIdx = layout.indexOfChild(layout.getChildAt(1))
                layout.addView(textView, baseIdx + 1)
            }
        }

        new Husk(patcher);

    override fun stop(ctx: Context) = patcher.unpatchAll()
}