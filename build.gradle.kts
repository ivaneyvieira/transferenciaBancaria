import Build_gradle.Defs.vaadin10_version
import Build_gradle.Defs.vaadinonkotlin_version
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object Defs {
  const val vaadinonkotlin_version = "1.1.0"
  const val vaadin10_version = "14.10.3"
  const val kotlin_version = "1.6.21"
  const val vaadin_plugin = "0.14.10.4"
}

plugins {
  kotlin("jvm") version "1.6.21"
  id("org.gretty") version "3.0.6"
  war
  id("com.vaadin") version "0.14.10.4"

  id("org.springframework.boot") version "2.7.14"
  id("io.spring.dependency-management") version "1.0.15.RELEASE"
  kotlin("plugin.spring") version "1.6.21"
}

defaultTasks("clean", "build")

repositories {
  mavenLocal()
  mavenCentral()
  jcenter()
  maven {
    url = uri("https://maven.vaadin.com/vaadin-addons")
  }
}

gretty {
  contextPath = "/"
  servletContainer = "jetty9.4"
}

val staging: Configuration by configurations.creating

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

group = "transferenciaBancaria"
version = "1.0"

java.sourceCompatibility = JavaVersion.VERSION_1_8

dependencies {
  //Spring
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.session:spring-session-core")
  providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

  implementation("com.github.mvysny.karibudsl:karibu-dsl:${vaadinonkotlin_version}") // Vaadin 14
  implementation("com.vaadin:vaadin-core:${vaadin10_version}") { // Webjars are only needed when running in Vaadin 13 compatibility mode
    listOf(
      "com.vaadin.webjar",
      "org.webjars.bowergithub.insites",
      "org.webjars.bowergithub.polymer",
      "org.webjars.bowergithub.polymerelements",
      "org.webjars.bowergithub.vaadin",
      "org.webjars.bowergithub.webcomponents"
    ).forEach { exclude(group = it) }
  }
  providedCompile("javax.servlet:javax.servlet-api:3.1.0")

  implementation("com.zaxxer:HikariCP:3.4.1")
  // logging
  // currently we are logging through the SLF4J API to LogBack. See src/main/resources/logback.xml file for the logger configuration
  implementation("ch.qos.logback:logback-classic:1.2.3")
  implementation("org.slf4j:slf4j-api:1.7.30")
  //implementation("org.slf4j:slf4j-simple:1.7.30")
  implementation("org.sql2o:sql2o:1.6.0")
  implementation("mysql:mysql-connector-java:5.1.48")
  implementation("com.zaxxer:HikariCP:3.4.1")
  implementation("org.imgscalr:imgscalr-lib:4.2")
  implementation("com.jcraft:jsch:0.1.55")
  implementation("org.cups4j:cups4j:0.7.6")
  // logging
  // currently we are logging through the SLF4J API to SLF4J-Simple. See src/main/resources/simplelogger.properties file for the logger configuration
  //implementation("com.github.appreciated:app-layout-addon:3.0.0.beta5")
  implementation("org.vaadin.tatu:twincolselect:1.2.0")
  implementation("org.vaadin.gatanaso:multiselect-combo-box-flow:1.1.0")
  implementation("org.vaadin.tabs:paged-tabs:2.0.1")
  implementation("org.claspina:confirm-dialog:2.0.0")
  implementation("org.vaadin.olli:clipboardhelper:1.1.2")
  implementation("com.flowingcode.addons:font-awesome-iron-iconset:2.1.1")
  //implementation("org.vaadin.miki:superfields:0.7.3")
  //  compile("org.webjars.bowergithub.vaadin:vaadin-combo-box:4.2.7")
  //compile("com.github.appreciated:app-layout-addon:4.0.0.rc4")
  implementation("org.vaadin.crudui:crudui:4.1.0")
  //compile("com.flowingcode.addons.applayout:app-layout-addon:2.0.2")
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))
}

vaadin {
  pnpmEnable = false
  productionMode = true
}