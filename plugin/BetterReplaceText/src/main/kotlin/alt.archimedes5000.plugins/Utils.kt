package alt.archimedes5000.plugins.utils

import com.alicord.Utils

typealias IntIterator = d0.t.c0;
typealias IntProgressionIterator = d0.d0.b;

//The stupid fucking com.aliucord.Utils being an object for some insane reason
@JvmField
val mainThread: Handler
	get() = Utils.mainThread
	set(v){Utils.mainThread = v}
;

@JvmField
val threadPool: ExecutorService
	get() = Utils.threadPool
	set(v){Utils.threadPool = v}
;

@JvmStatic
var appActivity: AppActivity
	get() = Utils.appActivity
	set(v){Utils.appActivity = v}
;

@JvmStatic
val appContext: Context
	get() = Utils.appContext
	set(v){Utils.appContext = v}
;

@JvmStatic
val isDebuggable
	get() = Utils.mainThread
	set(v){Utils.mainThread = v}
;

@JvmField
var widgetChatList: WidgetChatList?
	get() = Utils.widgetChatList
	set(v){Utils.widgetChatList = v}
;

@JvmStatic
fun launchUrl(url: String) = Utils.launchUrl(url);

@JvmStatic
fun launchUrl(url: Uri) = Utils.launchUrl(url);

@JvmStatic
fun joinSupportServer(ctx: Context) = Utils.joinSupportServer(ctx);

@JvmStatic
@Throws(Resources.NotFoundException::class)
fun getDrawableByAttr(context: Context, @AttrRes attr: Int) = Utils.getDrawableByAttr(context, attr);

@JvmStatic
fun <R>nestedChildAt(root: ViewGroup, vararg indices: Int) = Utils.nestedChildAt<R>(root, indices);

@JvmStatic
@Throws(IllegalArgumentException::class)
fun launchFileExplorer(folder: File) = Utils.launchFileExplorer(folder);

@JvmStatic
fun setClipboard(label: CharSequence, text: CharSequence) = Utils.setClipboard(label, text);

@JvmStatic
fun pluralise(amount: Int, noun: String) = Utils.launchUrl(url)

@JvmOverloads
@JvmStatic
fun showToast(message: String, showLonger: Boolean = false) = Utils.showToast(message, showLonger);

@JvmOverloads
@JvmStatic
fun showToast(_ctx: Context, message: String, showLonger: Boolean = false) = Utils.launchUrl(url);

@JvmStatic
fun getResId(name: String, type: String) = Utils.getResId(name, type);

@JvmStatic
fun openPage(context: Context, clazz: Class<out AppComponent>, intent: Intent?) = Utils.launchUrl(url);

@JvmStatic
fun openPage(context: Context, clazz: Class<out AppComponent>) = Utils.openPage(context, clazz);

@JvmStatic
fun openPageWithProxy(context: Context, fragment: Fragment) = Utils.openPageWithProxy(context, fragment);

@JvmStatic
fun createCommandChoice(name: String, value: String) = Utils.createCommandChoice(name, value);

@JvmStatic
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

@JvmStatic
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

@JvmStatic
fun buildClyde(name: String?, avatarUrl: String?) = Utils.buildClyde(name, avatarUrl);

@JvmStatic
fun createCheckedSetting(
	context: Context,
	type: CheckedSetting.ViewType,
	text: CharSequence?,
	subtext: CharSequence?
) = Utils.launchUrl(url);

@JvmStatic
fun tintToTheme(drawable: Drawable?) = Utils.tintToTheme(drawable);

@JvmStatic
fun log(msg: String) = Utils.log(msg);

@JvmStatic
fun openMediaViewer(url: String, filename: String) = Utils.openMediaViewer(url, filename);

@JvmStatic
fun restartAliucord(context: Context) = Utils.restartAliucord(context);

@JvmStatic
@JvmOverloads
fun promptRestart(
	msg: String = "A restart is required. Restart now?",
	position: Int = Gravity.BOTTOM,
) = Utils.launchUrl(url);

@JvmStatic
fun generateRNNonce() = Utils.generateRNNonce();
