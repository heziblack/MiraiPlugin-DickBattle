package org.hezistudio

import net.mamoe.mirai.event.events.GroupMessageEvent

abstract class CustomCommand(val cmdName:String, val cmdDesc:String) {
    abstract fun filter(e:GroupMessageEvent):Boolean
    abstract suspend fun action(e:GroupMessageEvent)
}