package org.hezistudio.data

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import org.hezistudio.data.CustomReply.provideDelegate

/**插件配置*/
object PluginConfig:AutoSavePluginConfig("pluginConfig"){
    var owner:Long by value(-1L)
    var groups:List<Long> by value(listOf())
}

/**自定义回复内容*/
object CustomReply:AutoSavePluginData("直接回复文本"){
    /**没有找到对手回复*/
    var NoneTarget:List<String> by value(listOf(
        "你要找谁比划啊？我不知道呢"
    ))

    /**用户没有牛牛回复*/
    var UserNoneDick:List<String> by value(listOf(
        "你没有牛牛是想准备用什么跟人家比划呢？"
    ))

    /**对手没有牛牛回复*/
    var TargetNoneDick:List<String> by value(listOf(
        "对方没有牛牛，你不能跟【ta】比划"
    ))

    var alreadyOwnedDick:List<String> by value(listOf(
        "你已经有【用户牛牛】了，居然还想着要新的牛牛，真是人间之屑~"
    ))

    val battleWinOrLose:List<String> by value(listOf(
        "【赢家】战胜了【输家】，获得【奖励名】【奖励值】"
    ))

    val battleDraw:List<String> by value(listOf(
        "【用户牛牛】跟【对手牛牛】两败俱伤，获得【用户惩罚名】【用户惩罚值】"
    ))
}

object CustomNoRenderReply:AutoSavePluginData("回复文本模板"){
    /**对手没有牛牛回复*/
    val TargetNoneDick:List<String> by value(listOf(
        "对方没有牛牛，你不能跟【ta】比划"
    ))

    val alreadyOwnedDick:List<String> by value(listOf(
        "你已经有【用户牛牛】了，居然还想着要新的牛牛，真是人间之屑~"
    ))

    val battleWinOrLose:List<String> by value(listOf(
        "【赢家】战胜了【输家】，获得【奖励名】【奖励值】"
    ))

    val battleDraw:List<String> by value(listOf(
        "【用户牛牛】跟【对手牛牛】两败俱伤，获得【用户惩罚名】【用户惩罚值】"
    ))
}

/**自定义起名文本库*/
object CustomNamer:AutoSavePluginData("起名库"){
    var adj:List<String> by value(listOf(
        "无坚不摧","可爱","外向","善良","开朗","活泼","好动","轻松","愉快",
        "可亲","豁达","稳重","幽默","真诚","豪爽","耿直","成熟","独立",
        "果断","健谈","机敏","深沉","坚强","兴奋","热情","率直","毅力",
        "友爱","风趣","沉静","谨慎","忠诚","友善","严肃","忠心","热情",
        "乐观","坦率","勇敢","自信","自立","沉著","执著","容忍","体贴",
        "积极","有趣","知足","勤劳","和气","无畏","务实","满足","弱智",
        "好交际","善组织","有韧性","可依赖","规范型","好心肠","善交际",
        "无异议","好胜","自律","受尊重","激励","重秩序","有条理","善于聆听",
        "无拘束","领导性","受欢迎","神经质","糊涂","懒惰","易兴奋","好批评",
        "不专注","好争吵","无目标","不宽恕","无热忱","易激动","难预测","不合群",
        "不灵活","喜操纵","情绪化","大嗓门","统治欲","强迫性","好表现",
    ))
    var names:List<String> by value(listOf(
        "大大怪将军","大山雀","牛牛","黑旋风","弟弟","鼓锤","小宝贝","小猫咪",
        "公鸡","匹诺曹","象拔蚌","金针菇","雄鹰","啄木鸟","铁杵","追风者","扣痞子",
        "挂马子","小兄弟","双汇","朖鸟","枪","鸡仔","泉水叮咚","大象"
    ))
}