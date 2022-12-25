package org.hezistudio.data

import org.jetbrains.exposed.dao.id.IntIdTable

object TableUsers: IntIdTable(){
    val nick = varchar("nick_name",64)
    val qqNumber = long("qq_number")
    val signGroup = long("sign_group")
    val gender = bool("gender")
    val companionUser = optReference("companion", TableUsers)
}

object TableDicks:IntIdTable(){
    val name = varchar("name",64)
    val gender = bool("gender")
    val owner = reference("owner", TableUsers)
    // 长度
    val length = double("length")
    // 硬度
    val hardness = double("hardness")
    // 柔韧度
    val pliable = double("pliable")
    // 架势：posture
    val posture = integer("posture")
    // 决斗cd
    val battleCd = long("bcd")
    // 贴贴cd
    val tieTieCd = long("tcd")
    // 生产Cd
    val produceCd = long("pcd")
}