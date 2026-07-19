package alt.archimedes5000.plugins.utils

import com.aliucord.Utils;
import com.aliucord.utils.ViewUtils;

import android.net.Uri;
import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.AttrRes;
import android.view.ViewGroup;
import java.io.File;
import com.discord.app.AppComponent;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.api.commands.CommandChoice;
import com.discord.models.commands.ApplicationCommandOption;
import com.discord.views.CheckedSetting;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.aliucord.Utils;
import com.aliucord.utils.ViewUtils.findViewById;
import com.discord.views.CheckedSetting;

//The stupid fucking com.aliucord.Utils being an object for some insane reason
@JvmField
val mainThread = Utils.mainThread;

@JvmField
val threadPool = Utils.threadPool;

var appActivity = Utils.appActivity;

val appContext = Utils.appContext;

val isDebuggable = Utils.isDebuggable;

@JvmField
var widgetChatList = Utils.widgetChatList;

fun launchUrl(url: String) = Utils.launchUrl(url);

fun launchUrl(url: Uri) = Utils.launchUrl(url);

fun joinSupportServer(ctx: Context) = Utils.joinSupportServer(ctx);

@Throws(Resources.NotFoundException::class)
fun getDrawableByAttr(context: Context, @AttrRes attr: Int) = Utils.getDrawableByAttr(context, attr);

fun <R>nestedChildAt(root: ViewGroup, vararg indices: Int) = Utils.nestedChildAt<R>(root, *indices);

@Throws(IllegalArgumentException::class)
fun launchFileExplorer(folder: File) = Utils.launchFileExplorer(folder);

fun setClipboard(label: CharSequence, text: CharSequence) = Utils.setClipboard(label, text);

fun pluralise(amount: Int, noun: String) = Utils.pluralise(amount, noun);

@JvmOverloads
fun showToast(message: String, showLonger: Boolean = false) = Utils.showToast(message, showLonger);

@JvmOverloads
fun showToast(_ctx: Context, message: String, showLonger: Boolean = false) = Utils.showToast(_ctx, message, showLonger);

fun getResId(name: String, type: String) = Utils.getResId(name, type);

fun openPage(context: Context, clazz: Class<out AppComponent>, intent: Intent?) = Utils.openPage(context, clazz, intent);

fun openPage(context: Context, clazz: Class<out AppComponent>) = Utils.openPage(context, clazz);

fun openPageWithProxy(context: Context, fragment: Fragment) = Utils.openPageWithProxy(context, fragment);

fun createCommandChoice(name: String, value: String) = Utils.createCommandChoice(name, value);

@JvmOverloads
fun createCommandOption(
	type: ApplicationCommandType = ApplicationCommandType.STRING,
	name: String,
	description: String? = null,
	descriptionRes: Int? = null,
	required: Boolean = false,
	default: Boolean = false,
	channelTypes: List<Int?> = emptyList(),
	choices: List<CommandChoice> = emptyList(),
	subCommandOptions: List<ApplicationCommandOption> = emptyList(),
	autocomplete: Boolean = false
) = Utils.createCommandOption(
	type,
	name,
	description,
	descriptionRes,
	required,
	default,
	channelTypes,
	choices,
	subCommandOptions,
	autocomplete
);

fun createCommandOption(
	type: ApplicationCommandType = ApplicationCommandType.STRING,
	name: String,
	description: String? = null,
	descriptionRes: Int? = null,
	required: Boolean = false,
	default: Boolean = false,
	channelTypes: List<Int?> = emptyList(),
	choices: List<CommandChoice> = emptyList(),
	subCommandOptions: List<ApplicationCommandOption> = emptyList(),
	autocomplete: Boolean = false,
	minValue: Number? = null,
	maxValue: Number? = null
) = Utils.createCommandOption(
	type,
	name,
	description,
	descriptionRes,
	required,
	default,
	channelTypes,
	choices,
	subCommandOptions,
	autocomplete,
	minValue,
	maxValue
);

fun buildClyde(name: String?, avatarUrl: String?) = Utils.buildClyde(name, avatarUrl);

fun createCheckedSetting(
	context: Context,
	type: CheckedSetting.ViewType,
	text: CharSequence?,
	subtext: CharSequence?
) = Utils.createCheckedSetting(context, type, text, subtext);

fun tintToTheme(drawable: Drawable?) = Utils.tintToTheme(drawable);

fun log(msg: String) = Utils.log(msg);

fun openMediaViewer(url: String, filename: String) = Utils.openMediaViewer(url, filename);

fun restartAliucord(context: Context) = Utils.restartAliucord(context);

@JvmOverloads
fun promptRestart(msg: String = "A restart is required. Restart now?", position: Int = Gravity.BOTTOM) = Utils.promptRestart(msg, position);

fun generateRNNonce() = Utils.generateRNNonce();

//fuck ViewUtils too
inline fun <T :View>T.addTo(group: ViewGroup, block: T.() -> Unit = {}): T = ViewUtils.addTo(this, group, block);

inline fun <T :View>T.addTo(group: ViewGroup, index: Int, block: T.() -> Unit = {}): T  = ViewUtils.addTo(this, group, index, block);

fun <T :View>T.setDefaultMargins(
	bottom: Boolean = true,
	top: Boolean = false,
	left: Boolean = true,
	right: Boolean = true
): T = ViewUtils.setDefaultMargins(bottom, top, left, right);

val CheckedSetting.layout get() = with(ViewUtils){layout};

val CheckedSetting.label get() = with(ViewUtils){label};

val CheckedSetting.checkbox get() = with(ViewUtils){checkbox};

val CheckedSetting.subtext get() = with(ViewUtils){subtext};

fun View.setPadding(value: Int) = ViewUtils.setPadding(value);

inline var View.leftPadding
	get() = with(ViewUtils){leftPadding}
	set(v) = with(ViewUtils){leftPadding = v}
;

inline var View.topPadding 
	get() = with(ViewUtils){topPadding}
	set(v) = with(ViewUtils){topPadding = v}
;

inline var View.rightPadding 
	get() = with(ViewUtils){rightPadding}
	set(v) = with(ViewUtils){rightPadding = v}
;

inline var View.bottomPadding 
	get() = with(ViewUtils){bottomPadding}
	set(v) = with(ViewUtils){bottomPadding = v}
;

inline var View.startPadding 
	get() = with(ViewUtils){startPadding}
	set(v) = with(ViewUtils){startPadding = v}
;

inline var View.endPadding
	get() = with(ViewUtils){endPadding}
	set(v) = with(ViewUtils){endPadding = v}
;

inline var View.padding 
	get() = with(ViewUtils){padding}
	set(v) = with(ViewUtils){padding = v}
;
