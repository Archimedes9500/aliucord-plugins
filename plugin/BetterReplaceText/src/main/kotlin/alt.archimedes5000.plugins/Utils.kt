package alt.archimedes5000.plugins.utils

import com.aliucord.Utils

typealias IntIterator = d0.t.c0;
typealias IntProgressionIterator = d0.d0.b;

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

fun <R>nestedChildAt(root: ViewGroup, vararg indices: Int) = Utils.nestedChildAt<R>(root, indices);

@Throws(IllegalArgumentException::class)
fun launchFileExplorer(folder: File) = Utils.launchFileExplorer(folder);

fun setClipboard(label: CharSequence, text: CharSequence) = Utils.setClipboard(label, text);

fun pluralise(amount: Int, noun: String) = Utils.launchUrl(url)

@JvmOverloads
fun showToast(message: String, showLonger: Boolean = false) = Utils.showToast(message, showLonger);

@JvmOverloads
fun showToast(_ctx: Context, message: String, showLonger: Boolean = false) = Utils.launchUrl(url);

fun getResId(name: String, type: String) = Utils.getResId(name, type);

fun openPage(context: Context, clazz: Class<out AppComponent>, intent: Intent?) = Utils.launchUrl(url);

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
