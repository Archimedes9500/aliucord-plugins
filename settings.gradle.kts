rootProject.name = "AliucordPlugins"

include(":MessageAnchor")
project(":MessageAnchor").projectDir = file("./plugins/MessageAnchor")
include(":MyFirstCommand")
project(":MyFirstCommand").projectDir = file("./plugins/MyFirstCommand")
include(":MyFirstPatch")
project(":MyFirstPatch").projectDir = file("./plugins/MyFirstPatch")

