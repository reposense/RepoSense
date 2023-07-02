import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.Exec
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.api.tasks.testing.Test
import com.github.psxpaul.task.JavaExecFork
import org.gradle.api.tasks.Copy
import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure

plugins {
    application
    checkstyle
    idea
    jacoco
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.liferay.node") version "7.2.18"
    id("com.github.psxpaul.execfork") version "0.2.0"
    id("com.palantir.git-version") version "0.13.0"
}

val os: OperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()

application {
    mainClass.set("reposense.RepoSense")
}

node {
    setDownload(false) // The Liferay Node Gradle Plugin will use the system PATH to find the Node/npm executable.
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

val testRuntime = configurations.create("testRuntime")

val systemtestImplementation = configurations.create("systemtestImplementation")
val systemtestRuntime = configurations.create("systemtestRuntime")

configurations {
    named("systemtestImplementation") {
        extendsFrom(configurations.getByName("testImplementation"))
    }
    named("systemtestRuntime") {
        extendsFrom(configurations.getByName("testRuntime"))
    }
}

dependencies {
    val jUnitVersion: String = "5.8.2"
    implementation(group = "com.google.code.gson", name = "gson", version = "2.9.0")
    implementation(group = "net.freeutils", name = "jlhttp", version = "2.6")
    implementation(group = "net.sourceforge.argparse4j", name = "argparse4j", version = "0.9.0")
    implementation(group = "org.apache.ant", name = "ant", version = "1.10.12")
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.9.0")
    implementation(group = "org.fusesource.jansi", name = "jansi", version = "2.4.0")
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = jUnitVersion)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = jUnitVersion)
}

sourceSets {
    val systemtest by creating {
        compileClasspath += sourceSets.getByName("main").output + sourceSets.getByName("test").output
        runtimeClasspath += sourceSets.getByName("main").output + sourceSets.getByName("test").output
        java.srcDir("src/systemtest/java")
        resources.srcDir("src/systemtest/resources")
    }
}

val installFrontend = tasks.register<Exec>("installFrontend") {
    setCommandLine("cmd")

    setWorkingDir("frontend/")
    setArgs(listOf("/c", "npm", "ci", "--production", "false", "--loglevel", "info", "--progress", "true"))
}

val buildFrontend = tasks.register<Exec>("buildFrontend") {
    setDependsOn(listOf(installFrontend))
    setCommandLine("cmd")

    setWorkingDir("frontend/")
    setArgs(listOf("/c", "npm", "run", "devbuild", "--production", "false", "--loglevel", "info", "--progress", "true"))
}

val zipReport = tasks.register<Zip>("zipReport") {
    setDependsOn(listOf(buildFrontend))
    from("frontend/build/")

    archiveBaseName.set("templateZip")
    destinationDirectory.set(file("src/main/resources"))
}

val copyCypressConfig = tasks.register<Copy>("copyCypressConfig") {
    description = "Copies the config files used by the backend to generate the test report for Cypress testing into an isolated working directory"

    from("frontend/cypress/config/")
    into("build/serveTestReport/exampleconfig/")
}

val copyMainClasses = tasks.register<Copy>("copyMainClasses") {
    description = "Copies the backend classes used to generate the test report for Cypress testing into an isolated working directory"
    dependsOn(tasks.named("classes"))

    from("build/classes/java/main/")
    into("build/serveTestReport/java/main/")
}

val compileJava = tasks.compileJava

tasks.named<ProcessResources>("processSystemtestResources").configure {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.compileJava {
    mustRunAfter(zipReport)
}

tasks.named("run") {
    dependsOn(zipReport)

    /* The second arguments indicate the default value associated with the property. */
    doFirst {
        val args = System.getProperty("args", "").split("")
        System.setProperty("version", getRepoSenseVersion())
    }
}

checkstyle {
    toolVersion = "9.3"
    getConfigDirectory().set(file("${rootProject.projectDir}/config/checkstyle"))
}

idea {
    module {
        sourceSets {
            named("systemtest") {
                allSource.srcDirs.forEach { srcDir ->
                    testSourceDirs = testSourceDirs.plus(srcDir)
                }
            }
        }
    }
}

tasks.test {
    systemProperty("REPOSENSE_ENVIRONMENT", "TEST")

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }

    doFirst {
        deleteReposAddressDirectory()
    }

    useJUnitPlatform()

    doLast {
        deleteReposAddressDirectory()
    }
}

tasks.shadowJar {
    dependsOn(zipReport)
}

tasks.compileJava {
    mustRunAfter("zipReport")
}

tasks.processResources {
    mustRunAfter("zipReport")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName.set("RepoSense.jar")
    destinationDirectory.set(file("${buildDir}/jar/"))

    manifest.attributes["Implementation-Version"] = getRepoSenseVersion()
}

tasks.register<Exec>("lintFrontend") {
    setDependsOn(listOf(installFrontend))
    setCommandLine("npm.cmd")

    setWorkingDir("frontend/")
    setArgs(listOf("run", "lint"))
}

val checkstyleMain = tasks.named("checkstyleMain")
val checkstyleTest = tasks.named("checkstyleTest")
val checkstyleSystemtest = tasks.named("checkstyleSystemtest")

tasks.named("checkstyleTest") {
    mustRunAfter(checkstyleMain)
}

tasks.named("checkstyleSystemtest") {
    mustRunAfter(checkstyleTest)
}

tasks.register<Checkstyle>("checkstyleAll") {
    setDependsOn(listOf(checkstyleMain, checkstyleTest, checkstyleSystemtest))
}

tasks.register<Exec>("environmentalChecks") {
    setWorkingDir("config/checks/")
    if (os.isWindows()) {
        setCommandLine(listOf("cmd", "/c", "run-checks.bat"))
    } else {
        setCommandLine(listOf("sh", "./run-checks.sh"))
    }
}

val systemtest = tasks.register<Test>("systemtest") {
    testClassesDirs = sourceSets.getByName("systemtest").output.classesDirs
    classpath = sourceSets.getByName("systemtest").runtimeClasspath

    systemProperty("REPOSENSE_ENVIRONMENT", "TEST")

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
    doFirst {
        deleteReposAddressDirectory()
    }

    useJUnitPlatform()

    doLast {
        deleteReposAddressDirectory()
    }
}

tasks.compileJava {
    mustRunAfter(zipReport)
}

tasks.processResources {
    mustRunAfter(zipReport)
}

val serveTestReportInBackground = tasks.register<JavaExecFork>("serveTestReportInBackground") {
    description = "Creates a background server process for the test report that is to be used by Cypress"
    dependsOn(
            tasks.getByName("zipReport"),
            tasks.getByName("compileJava"),
            tasks.getByName("processResources"),
            tasks.getByName("copyCypressConfig"),
            tasks.getByName("copyMainClasses")
    )

    setWorkingDir("build/serveTestReport/")
    main = application.getMainClass().get()
    classpath = sourceSets.getByName("main").runtimeClasspath
    args = listOf("--config", "./exampleconfig", "--since", "d1", "--view").toMutableList()

    val versionJvmArgs: String = "-Dversion=" + getRepoSenseVersion()
    jvmArgs = listOf(versionJvmArgs)
    killDescendants = false // Kills descendants of started process using methods only found in Java 9 and beyond.
    // Set to true by default but is incompatible with Java 8.
    // It should be removed from this file if we fully migrate to Java 11.
    waitForPort = 9000
}

val installCypress = tasks.register<Exec>("installCypress") {
    setCommandLine("cmd")

    setWorkingDir("frontend/cypress/")
    setArgs(listOf("/c", "npm", "ci", "--production", "false", "--loglevel", "info", "--progress", "true"))
}

tasks.named("serveTestReportInBackground") {
    mustRunAfter(installCypress)
}

tasks.register<Exec>("cypress") {
    dependsOn(listOf(installCypress, serveTestReportInBackground))
    setCommandLine("cmd")

    setWorkingDir("frontend/cypress")
    setArgs(listOf("/c", "npm", "run-script", "debug", "--production", "false", "--loglevel", "info", "--progress", "true"))
}

val frontendTest = tasks.register<Exec>("frontendTest") {
    setDependsOn(listOf(installCypress, serveTestReportInBackground))
    setCommandLine("cmd")

    setWorkingDir("frontend/cypress/")
    setArgs(listOf("/c", "npm", "run-script", "tests", "--production", "false", "--loglevel", "info", "--progress", "true"))
    if (project.hasProperty("ci")) {
        setArgs(listOf("/c", "npm", "run-script", "ci", "--production", "false", "--loglevel", "info", "--progress", "true"))
    }
}

tasks.withType<Copy>() {
    includeEmptyDirs = true
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.named<JacocoReport>("jacocoTestReport").configure {
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(true)
        html.destination = file("${buildDir}/jacocoHtml")
    }
    executionData(systemtest, frontendTest)
}

tasks.register<JacocoReport>("coverage").configure {
    sourceDirectories.from(files(sourceSets.getByName("main").allSource.srcDirs))
    classDirectories.from(files(sourceSets.getByName("main").output))
    executionData(systemtest, frontendTest)

    afterEvaluate {
        classDirectories.setFrom(classDirectories.files.forEach { dir: File ->
            fileTree(dir) {
                exclude("**/*.jar")
            }
        })
    }

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

fun getRepoSenseVersion(): String {
    var reposenseVersion = project.property("version") as String
    if (reposenseVersion.equals("unspecified")) {
        val versionDetails: Closure<VersionDetails> by extra
        val details = versionDetails()
        if (details.commitDistance == 0) {
            reposenseVersion = details.lastTag
        } else {
            reposenseVersion = details.gitHash
        }
    }
    return reposenseVersion
}

val syncFrontendPublic = tasks.register<Sync>("syncFrontendPublic") {
    from("reposense-report")
    into("reposense/public/")
    include("**/*.json")
    includeEmptyDirs = false
    preserve {
        include("index.html")
        include("favicon.ico")
    }
}

val macHotReloadFrontend = tasks.register<Exec>("macHotReloadFrontend") {
    setDependsOn(listOf(installFrontend))
    onlyIf {os.isMacOsX()}

    setWorkingDir("frontend/")
    setCommandLine("npm", "run", "serveOpen")
}

val windowsHotReloadFrontend = tasks.register<Exec>("windowsHotReloadFrontend") {
    setDependsOn(listOf(installFrontend))
    onlyIf {os.isWindows()}
    setWorkingDir("frontend/")
    setCommandLine("cmd", "/c", "START", "'hotreload RepoSense frontend'", "npm", "run", "serveOpen")
}

val linuxHotReloadFrontend = tasks.register<Exec>("linuxHotReloadFrontend") {
    setDependsOn(listOf(installFrontend))
    onlyIf {os.isLinux()}

    setWorkingDir("frontend/")
    setCommandLine("npm", "run", "serveOpen")
}

tasks.register("hotReloadFrontend") {
    setDependsOn(listOf(syncFrontendPublic))
    finalizedBy(macHotReloadFrontend)
    finalizedBy(windowsHotReloadFrontend)
    finalizedBy(linuxHotReloadFrontend)
}

fun deleteReposAddressDirectory() {
    val REPOS_ADDRESS = "repos"
    val reposDirectory = File(REPOS_ADDRESS)
    reposDirectory.deleteRecursively()
}

defaultTasks("clean", "build", "systemTest", "frontendTest", "coverage")