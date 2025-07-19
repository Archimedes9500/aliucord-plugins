rootProject.name = "AliucordPlugins"

include(":AnchorMessage")
project(":AnchorMessage").projectDir = file("./plugins/AnchorMessage")
include(":MyFirstCommand")
project(":MyFirstCommand").projectDir = file("./plugins/MyFirstCommand")
include(":MyFirstPatch")
project(":MyFirstPatch").projectDir = file("./plugins/MyFirstPatch")

