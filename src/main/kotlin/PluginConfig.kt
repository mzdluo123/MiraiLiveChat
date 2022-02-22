package win.rainchan.mirai.mirailivechat

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object PluginConfig : AutoSavePluginConfig("config") {
    var fileName by value<String>("live.txt")
    var group by value<Long>(0)
    var bufferSize by value<Int>(10)
    var template by value<String>("%name% 说: %msg%")
}
