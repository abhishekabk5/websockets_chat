import org.graalvm.buildtools.gradle.dsl.GraalVMExtension
import java.lang.System.getProperty

plugins {
    kotlin("jvm") version("1.8.20")
    id("org.graalvm.buildtools.native") version("0.9.21")
}

val hexagonVersion = "2.8.0"
val gradleScripts = "https://raw.githubusercontent.com/hexagonkt/hexagon/$hexagonVersion/gradle"

ext.set("options", "-Xmx48m")
ext.set("applicationClass", "com.hexagonkt.ws_chat.ChatPage")

apply(from = "$gradleScripts/kotlin.gradle")
apply(from = "$gradleScripts/application.gradle")
apply(from = "$gradleScripts/native.gradle")

defaultTasks("build")

version="1.0.0"
group="com.hexagonkt.ws_chat"
description="Service's description"

dependencies {
    implementation("com.hexagonkt:http_server_netty:$hexagonVersion")
    implementation("org.slf4j:slf4j-nop:2.0.7")
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation("com.hexagonkt:http_client_jetty:$hexagonVersion")
}

extensions.configure<GraalVMExtension> {
    fun option(name: String, value: (String) -> String): String? =
        getProperty(name)?.let(value)

    binaries {
        named("main") {
            listOfNotNull(
                "--static", // Won't work on Windows or macOS
                "-R:MaxHeapSize=16",
                option("enableMonitoring") { "--enable-monitoring" },
            )
            .forEach(buildArgs::add)
        }
    }
}
