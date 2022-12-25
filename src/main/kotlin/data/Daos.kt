package org.hezistudio.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class DaoUser(id:EntityID<Int>):IntEntity(id){
    companion object:IntEntityClass<DaoUser>(TableUsers)
    var nick by TableUsers.nick
    var qqNumber by TableUsers.qqNumber
    var signGroup by TableUsers.signGroup
    var gender by TableUsers.gender
    var companionUser by DaoUser optionalReferencedOn TableUsers.companionUser
}


class DaoDick(id: EntityID<Int>):IntEntity(id){
    companion object:IntEntityClass<DaoDick>(TableDicks)
    var name by TableDicks.name
    var gender by TableDicks.gender
    var owner by DaoUser referencedOn TableDicks.owner
    var length by TableDicks.length // 长度
    var hardness by TableDicks.hardness // 硬度
    var pliable by TableDicks.pliable // 柔韧度
    var posture by TableDicks.posture // 姿态
    var battleCd by TableDicks.battleCd
    var tieTieCd by TableDicks.tieTieCd
    var produceCd by TableDicks.produceCd
    var postureE:Posture
        get() = Posture.values()[posture]
        set(value) {
            transaction(db) { posture = value.ordinal }
        }

    var genderStr:String
        get() = if (gender) "女" else "男"
        set(value) {
            transaction(db) {
                when(value){
                    "男" -> gender = false
                    "女" -> gender = true
                }
            }
        }



}

