plugins {
    val kotlinVersion = "1.6.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.13.2"
}

group = "org.hezistudio"
version = "0.1.5-RC"

repositories {
    if (System.getenv("CI")?.toBoolean() != true) {
        maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    }
    mavenCentral()
}

dependencies{
    implementation("org.jetbrains.exposed","exposed-core","0.38.2")
    implementation("org.jetbrains.exposed","exposed-dao","0.38.2")
    implementation("org.jetbrains.exposed","exposed-jdbc","0.38.2")
    implementation ("com.google.code.gson:gson:2.9.1")
    implementation("org.xerial:sqlite-jdbc:3.40.0.0")
}