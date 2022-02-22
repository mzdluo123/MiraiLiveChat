package win.rainchan.mirai.mirailivechat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info
import java.io.File

/**
 * 使用 kotlin 版请把
 * `src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin`
 * 文件内容改成 `org.example.mirai.plugin.PluginMain` 也就是当前主类全类名
 *
 * 使用 kotlin 可以把 java 源集删除不会对项目有影响
 *
 * 在 `settings.gradle.kts` 里改构建的插件名称、依赖库和插件版本
 *
 * 在该示例下的 [JvmPluginDescription] 修改插件名称，id和版本，etc
 *
 * 可以使用 `src/test/kotlin/RunMirai.kt` 在 ide 里直接调试，
 * 不用复制到 mirai-console-loader 或其他启动器中调试
 */

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "win.rainchan.mirai.mirailivechat",
        name = "MiraiLiveChat",
        version = "0.1.0"
    )
) {
    lateinit var chatFile: File
    private val buffer = ArrayDeque<String>()
    override fun onEnable() {
        logger.info { "直播辅助 已加载" }
        PluginConfig.reload()
        chatFile = File(PluginConfig.fileName)
        if (!chatFile.exists()) {
            chatFile.createNewFile()
        }
        globalEventChannel().subscribeAlways<GroupMessageEvent> {
            if (this.group.id == PluginConfig.group) {
                val firstObj = this.message[1]
                if (firstObj is PlainText) {
                    val text =
                        PluginConfig.template.replace("%name%", this.senderName).replace("%msg%", firstObj.content)
                    buffer.add(text)
                    while (buffer.size >= PluginConfig.bufferSize){
                       buffer.removeFirst()
                    }
                    writeFile()
                }
            }
        }

    }

    private suspend fun writeFile(){
        withContext(Dispatchers.IO){
            chatFile.outputStream().use {
                val content = buffer.asSequence().joinToString("\n")
                it.write(content.toByteArray())
            }

        }
    }

}
