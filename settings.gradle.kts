rootProject.name = "AliucordPlugins"

include(":BetterFakeStickers")
project(":BetterFakeStickers").projectDir = file("./plugins/BetterFakeStickers")
include(":MessageAnchor")
project(":MessageAnchor").projectDir = file("./plugins/MessageAnchor")
include(":MyFirstCommand")
project(":MyFirstCommand").projectDir = file("./plugins/MyFirstCommand")
include(":MyFirstPatch")
project(":MyFirstPatch").projectDir = file("./plugins/MyFirstPatch")
include(":ReplyReferenceFix")
project(":ReplyReferenceFix").projectDir = file("./plugins/ReplyReferenceFix")

