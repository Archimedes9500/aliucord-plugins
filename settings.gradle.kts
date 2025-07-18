rootProject.name = "AliucordPlugins"

include(":MyFirstCommand")
project(":MyFirstCommand").projectDir = file("./plugins/MyFirstCommand")
include(":MyFirstPatch")
project(":MyFirstPatch").projectDir = file("./plugins/MyFirstPatch")
include(":Shit")
project(":Shit").projectDir = file("./plugins/Shit")

