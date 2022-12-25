package org.hezistudio.data

class MessageRender {
    /**
     * 【ta】 [tarUser]对手第三人称代词
     *
     * 【用户名】 [user]的名字
     *
     * 【用户牛牛】 [dick]的名字
     *
     * 【对手名】 [tarUser]的名字
     *
     * 【对手牛牛】 [tarDick]的名字
     *
     * */
    fun render(
        origin:String,
        user: DaoUser,
        dick: DaoDick? = null,
        tarUser: DaoUser? = null,
        tarDick: DaoDick? = null
    ):String{
        var temp = origin.replace("【用户名】",user.nick)
        if (dick != null){
            temp = temp.replace("【用户牛牛】",dick.name)
        }
        if (tarUser!=null){
            val ta = if(tarUser.gender) "女" else "男"
            temp = temp.replace("【ta】", ta).replace("【对手名】",tarUser.nick)
        }
        if (tarDick != null){
            temp = temp.replace("【对手牛牛】",tarDick.name)
        }
        return temp
    }

    /**
     * 【赢家】 [winner]的名字
     *
     * 【输家】 [loser]的名字
     * */
    fun battleRender(
        origin:String,
        winner: DaoDick,
        loser: DaoDick,
        gain: Double,
    ):String{
        return origin.replace("【赢家】",winner.name).replace("【输家】",loser.name)
    }



}