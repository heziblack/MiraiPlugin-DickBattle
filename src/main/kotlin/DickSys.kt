package org.hezistudio

import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.ListenerHost
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.*
import org.hezistudio.data.*
import java.text.NumberFormat
import java.time.Duration
import javax.swing.text.NumberFormatter
import kotlin.math.roundToLong

object DickSys:ListenerHost {

    val dbm = PluginMain.dbm

    /**浮点数格式*/
    private val nf = NumberFormat.getInstance()

    /**时间间隔转字符串*/
    fun durationToString(duration: Duration):String{
        val sb = StringBuilder()
        val day = duration.toDays()
        val hour = duration.toHours() - day * 24
        val minute = duration.toMinutes() - (day * 24 + hour) * 60
        val second = duration.seconds - ((day * 24 + hour) * 60 + minute)*60
        if (day!=0L) sb.append("${day}天")
        if (hour!=0L) sb.append("${hour}时")
        if (minute!=0L) sb.append("${minute}分")
        if (second!=0L) sb.append("${second}秒")
        return sb.toString()
    }

    init {
        nf.maximumFractionDigits = 2
    }

    /**名字生成*/
    private object NameGenerator {
        fun generateName():String{
            val sb = StringBuilder()
            if (Math.random()<0.2){
                sb.append(CustomNamer.adj.random())
                if (Math.random()>0.7){
                    sb.append("的")
                }else{
                    sb.append("の")
                }
            }
            sb.append(CustomNamer.names.random())
            return sb.toString()
        }
    }

    /**消息渲染*/
    private val msgRender = MessageRender()

    @EventHandler
    suspend fun onGroupMessageEvent(e:GroupMessageEvent){
        for (cmd in cmdList){
            if (cmd.filter(e)){
                cmd.action(e)
                break
            }
        }
    }

    private val battle = object: CustomCommand(
        "牛牛对决",
        "向你at的对象发起挑战\n例: 比划比划 @xxx")
    {
        /**过滤机制*/
        override fun filter(e: GroupMessageEvent): Boolean {
            val msg = e.message
            val at = msg.findIsInstance<At>() ?: return false
            if (at.target == e.bot.id) return false
            when (msg.size) {
                3 -> {
                    val pt = msg[1]
                    if (pt !is PlainText) return false
                    return Regex("""比划比划 *""").matches(pt.content)
                }
                4 -> {
                    val pt = msg[1]
                    if (pt !is PlainText) return false
                    val pt2 = msg[3]
                    if (pt2 !is PlainText) return false
                    return Regex("""比划比划 *""").matches(pt.content) && Regex("""\s*""").matches(pt2.content)
                }
                else -> {
                    return false
                }
            }
        }

        /**操作*/
        override suspend fun action(e: GroupMessageEvent) {
            // 获取或创建用户对象
            val user = dbm.addUser(e.sender)
            // 获取At对象, 因为已经过滤了所以是一定存在的
            val targetMemberNum = e.message.findIsInstance<At>()!!.target
            // 获取艾特的聊天对象（群员Member）
            val targetUserMember = e.group[targetMemberNum]
            // 获取艾特对象失败
            if (targetUserMember == null){
                e.group.sendMessage(msgRender.render(CustomReply.NoneTarget.random(),user))
                return
            }
            // 获取或创建用户的数据库对象
            val targetUser = dbm.addUser(targetUserMember)
            // 获取用户的牛牛信息
            val userDick = dbm.getDick(user)
            if (userDick == null){
//                val reply = msgRender.render(CustomReply.UserNoneDick.random(), user, tarUser = targetUser)
                val reply = "你没有牛牛，发送领养牛牛获取一个吧"
                e.group.sendMessage(reply)
                return
            }
            // 获取对手牛牛
            val targetDick = dbm.getDick(targetUser)
            if (targetDick == null){
                val reply = msgRender.render(CustomReply.TargetNoneDick.random(), user, userDick, targetUser)
                e.group.sendMessage(reply)
                return
            }
            // 用户cd检查
            val uDickCdLeft = dbm.checkBattleCD(userDick)
            if (!uDickCdLeft.isNegative){
                val time = durationToString(uDickCdLeft)
                val at = At(e.sender)
                e.group.sendMessage(messageChainOf(
                    at, PlainText("你的牛牛红肿了\n消肿时间：${time}")
                ))
                return
            }
            // 对手cd检查
            val tDickCdLeft = dbm.checkBattleCD(targetDick)
            if (!tDickCdLeft.isNegative){
                val time = durationToString(tDickCdLeft)
                val at = At(e.sender)
                e.group.sendMessage(messageChainOf(
                    at, PlainText("对方的牛牛红肿了\n消肿时间：${time}")
                ))
                return
            }
            // 对决&结算
            val result = dickBattle(userDick,targetDick)
            val reply = settlement(userDick,targetDick,result)

            // 设置冷却
            dbm.dickBattleColdDown(userDick)
            dbm.dickBattleColdDown(targetDick)

            // 回复
            e.group.sendMessage(reply)
        }

        /**牛牛对决，根据双方体态调整战斗力后计算战斗结果*/
        private fun dickBattle(challenger:DaoDick, target:DaoDick):Boolean?{
            // 战斗力 = 柔韧度 / (长度 * 硬度)
            var cBattleScore = challenger.pliable / (challenger.length * challenger.hardness)
            val tBattleScore = target.pliable / (target.length * target.hardness)
            when(challenger.postureE){
                Posture.P0 -> {
                    when(target.postureE){
                        Posture.P0 -> {}
                        Posture.P1 -> {
                            /*  犁式 -> 顶式： 进攻方70%概率获得加成 */
                            if(Math.random() > 0.3) cBattleScore *= 3.0
                        }
                        Posture.P2 -> {
                            /* 犁式 -> 愚者式: 进攻方50%概率获得加成 */
                            if(Math.random() > 0.5) cBattleScore *= 3.0
                        }
                        Posture.P3 -> {
                            /* 犁式 -> 牛式: 进攻方50%概率获得加成 */
                            if(Math.random() > 0.5) cBattleScore *= 3.0
                        }
                    }
                }
                Posture.P1 -> {
                    when(target.postureE){
                        Posture.P0 -> {
                            /* 顶式-犁式: 50% 倍率 4 */
                            if(Math.random() > 0.5) cBattleScore *= 4.0
                        }
                        Posture.P1 ->{}
                        Posture.P2 -> {
                            /* 顶式-愚者式: 80% 倍率 4 */
                            if(Math.random() > 0.2) cBattleScore *= 4.0
                        }
                        Posture.P3 -> {
                            /* 顶式-牛式: 20% 倍率 2 */
                            if(Math.random() > 0.8) cBattleScore *= 2.0
                        }
                    }
                }
                Posture.P2 -> {
                    when(target.postureE){
                        Posture.P0 -> {
                            /* 愚者式-犁式: 20% 倍率 2 */
                            if(Math.random() > 0.5) cBattleScore *= 3.0
                        }
                        Posture.P1 -> {
                            /* 愚者式-顶式: 80% 倍率 4 */
                            if(Math.random() > 0.2) cBattleScore *= 4.0
                        }
                        Posture.P2 -> {}
                        Posture.P3 -> {
                            /* 愚者式-牛式: 70% 倍率 4 */
                            if(Math.random() > 0.3) cBattleScore *= 3.3
                        }
                    }
                }
                Posture.P3 -> {
                    when(target.postureE){
                        Posture.P0 -> {
                            /* 牛式-犁式: 50% 倍率 3.5 */
                            if(Math.random() > 0.5) cBattleScore *= 3.5
                        }
                        Posture.P1 -> {
                            /* 牛式-顶式: 80% 倍率 3.5 */
                            if(Math.random() > 0.2) cBattleScore *= 3.5
                        }
                        Posture.P2 -> {
                            /* 牛式-愚者: 80% 倍率 3.5 */
                            if(Math.random() > 0.2) cBattleScore *= 3.5
                        }
                        Posture.P3 -> {}
                    }
                }
            }
            return compareBattleScore(cBattleScore, tBattleScore)
        }

        /**根据战力返回比较结果*/
        private fun compareBattleScore(u:Double,t:Double):Boolean?{
            val p = u/(u+t) // 战斗力占比
            val r = Math.random() // 随机数
            return if (p < 0.1){
                // 过于弱势
                if (r < p) true
                else if (r < (1.0-p)) false
                else null
            }else if(p<0.45)
                r >= 0.9 // 弱势
            else if (p<0.55){
                // 相差仿佛
                if (r < 0.4) false
                else if (r< 0.6) null
                else true
            }else if (p<0.9)
                r < 0.9 // 强势
            else{
                // 过于强势
                if (r > p) false
                else if (r > (1-p)) true
                else null
            }
        }

        /** 结算并返回结算结果文本 */
        private fun settlement(uDick:DaoDick, tDick: DaoDick, result:Boolean?):String{
            val sb = StringBuilder()
            when(result){
                true -> {
                    // 胜
                    sb.append("${uDick.name} 胜过了 ${ tDick.name }\n")
                    if (Math.random()<0.5){
                        // 长度
                        // 吸收数值
                        val a = (tDick.length * Math.random().coerceIn(0.05,0.2) * 100).roundToLong() / 100.0
                        // 数值足够
                        if (a > 0.1){
                            sb.append("吸收了对方${a}cm长度")
                            dbm.dickLengthChange(uDick, a)
                            dbm.dickLengthChange(tDick,-a)
                        }
                    }else{
                        //硬度
                        // 吸收数值
                        val a = (tDick.hardness * Math.random().coerceIn(0.05,0.1) * 100).roundToLong() / 100.0
                        // 数值足够
                        if (a > 0.1){
                            sb.append("吸收了对方${a}硬度")
                            dbm.dickHardnessChange(uDick, a)
                            dbm.dickHardnessChange(tDick,-a)
                        }
                    }
                }
                false ->{
                    // 负
                    sb.append("${uDick.name} 被 ${ tDick.name }击败了\n")
                    if (Math.random()<0.5){
                        // 吸收数值
                        val a = (uDick.length * Math.random().coerceIn(0.05,0.2) * 100).roundToLong() / 100.0
                        // 数值足够
                        if (a > 0.1){
                            sb.append("被夺走了${a}cm长度")
                            dbm.dickLengthChange(uDick,-a)
                            dbm.dickLengthChange(tDick, a)
                        }
                    }else{
                        //硬度
                        // 吸收数值
                        val a = (uDick.hardness * Math.random().coerceIn(0.05,0.1) * 100).roundToLong() / 100.0
                        // 数值足够
                        if (a > 0.1){
                            sb.append("被吸走了${a}硬度")
                            dbm.dickHardnessChange(uDick,-a)
                            dbm.dickHardnessChange(tDick, a)
                        }
                    }
                }
                null -> {
                    // 平
                    sb.append("${uDick.name} 与 ${ tDick.name } 不分胜负\n")
                    // 30%概率触发
                    if (Math.random()<0.3){
                        // 获得较短的长度值
                        val a = (uDick.length.coerceAtMost(tDick.length)) * Math.random().coerceIn(0.05,0.1) * 100
                        val b = a.roundToLong() / 100.0
                        sb.append("双方都断了${b}cm")
                    }
                }
            }
            return sb.toString()
        }
    }

    private val adopt = object: CustomCommand(
        "领养牛牛",
        "领养一只牛牛"
    ){
        override fun filter(e: GroupMessageEvent): Boolean {
            if (e.message.findIsInstance<QuoteReply>()!=null) return false
            return e.message.content == "领养牛牛"
        }

        override suspend fun action(e: GroupMessageEvent) {
            val user = dbm.addUser(e.sender)
            var dick = dbm.getDick(user)
            val at = At(e.sender)
            if (dick == null){
                dick = dbm.addDick(user, NameGenerator.generateName())
                val dickInfo = """
                    领养成功！
                    ${dick.name}(${dick.genderStr})
                    长度：${nf.format(dick.length)}
                    硬度：${nf.format(dick.hardness)}
                    韧性：${nf.format(dick.pliable)}
                    姿态:${dick.postureE.nameStr}
                """.trimIndent()
                e.group.sendMessage(messageChainOf(
                    at, PlainText(dickInfo)
                ))
            }else{
                val reply = msgRender.render(CustomReply.alreadyOwnedDick.random(),user,dick)
                e.group.sendMessage(messageChainOf(
                    at, PlainText(reply)
                ))
            }
        }
    }

    private val dickInfo = object :CustomCommand(
        "我的牛牛",
        "显示牛牛信息"){
        override fun filter(e: GroupMessageEvent): Boolean {
            val msg = e.message
            if (msg.findIsInstance<QuoteReply>()!=null) return false
            if (msg.content == "我的牛牛") {
                val u = dbm.addUser(e.sender)
                if (dbm.getDick(u)==null) return false
                return true
            }
            return false
        }

        override suspend fun action(e: GroupMessageEvent) {
            val user = dbm.addUser(e.sender)
            val dick = dbm.getDick(user)!!
            val at = At(e.sender)
            val dickInfo = """
                    ${dick.name}(${dick.genderStr})
                    长度：${nf.format(dick.length)}
                    硬度：${nf.format(dick.hardness)}
                    韧性：${nf.format(dick.pliable)}
                    姿态:${dick.postureE.nameStr}
                """.trimIndent()
            e.group.sendMessage(messageChainOf(
                at,PlainText("\n"),PlainText(dickInfo)
            ))
        }

    }

    private val cmdList = listOf<CustomCommand>(
        battle, adopt, dickInfo
    )
}