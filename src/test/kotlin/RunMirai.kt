package org.example.mirai.plugin

import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader
import org.hezistudio.data.DatabaseManager
import org.hezistudio.data.Posture
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

suspend fun main() {
    val dtf = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    val start = LocalDateTime.from(dtf.parse("221211150000"))
    val endTime = LocalDateTime.from(dtf.parse("221221170000"))
    val a = Duration.between(start,endTime)
    println(a.isNegative)
    val day = a.toDays()
    val hour = a.toHours() - day * 24
    val minute = a.toMinutes() - (day * 24 + hour) * 60
    val second = a.seconds - ((day * 24 + hour) * 60 + minute) * 60
    println("$day $hour $minute $second")





}