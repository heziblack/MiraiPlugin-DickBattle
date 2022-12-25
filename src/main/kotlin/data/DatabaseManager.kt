package org.hezistudio.data

import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.nameCardOrNick
import org.hezistudio.PluginMain
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToLong

/** 数据库代理 */
class DatabaseManager(ifTest:Boolean = false) {
    /** 数据库文件 */
    val dbFile:File

    /** 数据库连接 */
    private val db: Database

    /** 初始化 */
    init {
        dbFile = if (!ifTest){
            File(PluginMain.dataFolder, "database.db3")
        }else{
            val path = System.getProperty("user.dir")
            File(path,"test.sqlite")
        }
        createDatabaseFileIfNotExists()
        db = Database.connect("jdbc:sqlite:${dbFile.toURI()}","org.sqlite.JDBC")
        createTables()
    }

    /** 当数据库文件不存在时创建 */
    private fun createDatabaseFileIfNotExists(){
        if (!dbFile.exists()){
            val pFolder = dbFile.parentFile
            if(!pFolder.exists()){
                pFolder.mkdirs()
            }
            dbFile.createNewFile()
        }
    }

    /**创建数据库表*/
    private fun createTables(){
        transaction(db) {
            SchemaUtils.create(
                TableUsers,TableDicks
            )
        }
    }


    fun testInsertUser(uid:Long,gid:Long = uid):DaoUser{
        return transaction(db) {
            val query = DaoUser.find { TableUsers.qqNumber eq uid }.toList()
            if (query.isEmpty()){
                return@transaction DaoUser.new {
                    nick = "用户${uid}"
                    qqNumber = uid
                    signGroup = gid
                    gender = uid%2 == 0L
                    companionUser = null
                }
            }else{
                return@transaction query.first()
            }
        }
    }

    /**添加用户并返回，若存在则返回存在的用户*/
    fun addUser(member:Member):DaoUser{
        return transaction(db) {
            val query = DaoUser.find { TableUsers.qqNumber eq member.id }.toList()
            if (query.isEmpty()){
                return@transaction DaoUser.new {
                    nick = member.nameCardOrNick
                    qqNumber = member.id
                    signGroup = member.group.id
                    gender = Math.random() > 0.5
                    companionUser = null
                }
            }else{
                return@transaction query.first()
            }
        }
    }

    /**添加牛牛并返回，若存在则返回存在的牛牛*/
    fun addDick(owner:DaoUser, dickName:String):DaoDick{
        return transaction(db) {
            val query = DaoDick.find {
                TableDicks.owner eq owner.id
            }
            if (query.empty()){
                return@transaction DaoDick.new {
                    name = dickName
                    gender = owner.gender
                    this.owner = owner
                    length = 10.0
                    hardness = 11.0
                    pliable = 12.0
                    posture = 0
                    battleCd = -1L
                    tieTieCd = -2L
                    produceCd = -3L
                }
            }else{
                return@transaction query.first()
            }
        }
    }

    /**获取牛牛，若没有返回null*/
    fun getDick(user:DaoUser):DaoDick?{
        return transaction(db) {
            val query = DaoDick.find {
                TableDicks.owner eq user.id
            }
            if (query.empty()){
                return@transaction null
            }else{
                return@transaction query.first()
            }
        }
    }

    /**时间格式转化*/
    private val dtf = DateTimeFormatter.ofPattern("yyMMddHHmmss")

    /**检查决斗cd*/
    fun checkBattleCD(dick: DaoDick): Duration {
        if(dick.battleCd<=0L) return Duration.ZERO
        val endTime = LocalDateTime.from(dtf.parse(dick.battleCd.toString()))
        val current = LocalDateTime.now()
        return Duration.between(current,endTime)
    }

    /**改变牛牛长度，有设置与修改两种模式，默认修改*/
    fun dickLengthChange(dick:DaoDick,v:Double,isSet:Boolean=false){
        transaction(db) {
            if (isSet){
                dick.length = v
            }else{
                dick.length += v
            }
        }
    }

    /**改变牛牛长度，有设置与修改两种模式，默认修改*/
    fun dickHardnessChange(dick: DaoDick, v: Double, isSet: Boolean = false){
        transaction(db) {
            if (isSet){
                dick.hardness = v
            }else{
                dick.hardness += v
            }
        }
    }

    /**设置牛牛冷却时间*/
    fun dickBattleColdDown(dick:DaoDick){
        val cdStamp =
            if (dick.length < 1000){
            val hours = dick.length.roundToLong()
            val endTime = LocalDateTime.now().plusHours(hours)
            dtf.format(endTime).toLong()

        }else{
            val days = ((dick.length.roundToLong()-1000) / 30.0).coerceIn(1.0,30.0).roundToLong()
            val endTime = LocalDateTime.now().plusDays(days)
            dtf.format(endTime).toLong()
        }
        transaction(db) {
            dick.battleCd = cdStamp
        }
    }
}