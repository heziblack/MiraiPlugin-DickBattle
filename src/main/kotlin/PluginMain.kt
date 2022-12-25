package org.hezistudio

import net.mamoe.mirai.console.permission.AbstractPermitteeId
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info
import org.hezistudio.data.CustomNamer
import org.hezistudio.data.CustomReply
import org.hezistudio.data.DatabaseManager
import org.hezistudio.data.PluginConfig
import org.hezistudio.data.DatabaseManager as dbm

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
        id = "org.hezistudio.dick-battle",
        name = "牛牛对决",
        version = "0.1.5-RC"
    ) {
        author("Hezi")
        info(
            """
            牛牛对决插件
        """.trimIndent()
        )

    }
) {
    val dbm = DatabaseManager()
    override fun onEnable() {
        PluginConfig.reload() // 只需要reload便会自动创建文件
        logger.info("加载牛牛系统...")

        CustomNamer.reload()
        CustomReply.reload()
        logger.info("插件管理员：${PluginConfig.owner}")
        logger.info("插件工作群：${PluginConfig.groups.size}个")
        logger.info("起名描述${CustomNamer.adj.size}个，名字${CustomNamer.names.size}个")

        globalEventChannel().filter {
            if (it is GroupMessageEvent){
                (it as GroupMessageEvent).group.id in PluginConfig.groups
            }else{
                false
            }
        }.registerListenerHost(DickSys)
    }
}
