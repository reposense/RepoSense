import com.liferay.gradle.plugins.node.tasks.ExecutePackageManagerTask
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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

// allprojects {
//     plugins.withId("com.liferay.node") {
//         node.download = false
//     }
// }

val os : OperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem();

application {
    mainClass.set("reposense.Reposense")
}

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

configurations {
    create("systemtestImplementation") {
        extendsFrom(configurations.getByName("testImplementation"))
    }
    create("systemtestRuntime") {
        extendsFrom(configurations.getByName("runtimeClasspath"))
    }
}

dependencies {
    val jUnitVersion: String = "5.8.2"
    implementation(group = "com.google.code.gson", name = "gson", version ="2.9.0")
    implementation(group = "net.freeutils", name = "jlhttp", version = "2.6")
    implementation(group = "net.sourceforge.argparse4j", name = "argparse4j", version = "0.9.0")
    implementation(group = "org.apache.ant", name = "ant", version = "1.10.12")
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.9.0")
    implementation(group = "org.fusesource.jansi", name = "jansi", version = "2.4.0")
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = jUnitVersion)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = jUnitVersion)
}

sourceSets {
    create("systemtest") {
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        java.srcDir(file("src/systemtest/java"))
        resources.srcDir(file("src/systemtest/resources"))
    }
}

val installFrontend = tasks.register("installFrontend", ExecutePackageManagerTask::class) {
    setWorkingDir("frontend/")
    setCacheDir("frontend/")
    setArgs(listOf("ci"))
}

val buildFrontend = tasks.register("buildFrontend", ExecutePackageManagerTask::class) {
    dependsOn(installFrontend)
    setWorkingDir("frontend/")
    setArgs(listOf("run", "devbuild"))
}

val zipReport = tasks.register("zipReport", Zip::class) {
    dependsOn(buildFrontend)
    from("frontend/build/")
    archiveBaseName.set("templateZip")
    destinationDirectory.set(file("src/main/resources"))
}

val copyCypressConfig = tasks.register("copyCypressConfig", Copy::class) {
    description = "Copies the config files used by the backend to generate the test report for Cypress testing into an isolated working directory"
    from("frontend/cypress/config")
    into("build/serveTestReport/exampleconfig")
}

val copyMainClasses = tasks.register("copyMainClasses", Copy::class) {
    description = "Copies the backend classes used to generate the test report for Cypress testing into an isolated working directory"
    dependsOn(project.tasks.named("classes"))
    from("build/classes/java/main")
    into("build/serveTestReport/java/main")
}

val compileJava = tasks.named("compileJava")

val processResources = tasks.named<ProcessResources>("processResources") 

tasks.withType<ProcessResources>() {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named("run") {
    dependsOn(zipReport)
    tasks.named("compileJava") {
        mustRunAfter("zipReport")
    }
    val args = System.getProperty("args", "").split(" ")
    System.setProperty("version", getRepoSenseVersion())
}

checkstyle {
    toolVersion = "9.3"
    configDirectory.set(file("/config/checkstyle"))
}

idea {
    module {
        sourceSets.getByName("systemtest").allSource.srcDirs.forEach { srcDir ->
            testSourceDirs.plusAssign(srcDir)
        }
    }
}

tasks.register("systemtest", Test::class) {
    testClassesDirs = sourceSets.getByName("systemtest").output.classesDirs
    classpath = sourceSets.getByName("systemtest").runtimeClasspath
    environment("REPOSENSE_ENVIRONMENT", "TEST")
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

tasks.named<ShadowJar>("shadowJar") {
    dependsOn(zipReport)
    dependsOn(tasks.named("compileJava"), tasks.named("processResources"))
    archiveFileName.set("RepoSense.jar")
    destinationDirectory.set(file("${buildDir}/jar/"))

    manifest {
        attributes("Implementation-Version" to getRepoSenseVersion())
    }
}

tasks.register("lintFrontend", ExecutePackageManagerTask::class) {
    dependsOn(installFrontend)
    setWorkingDir("frontend/")
    setArgs(listOf("run", "lint"))
}

val checkstyleMain = tasks.named("checkstyleMain")
val checkstyleTest = tasks.named("checkstyleTest")
val checkstyleSystemtest = tasks.named("checkstyleSystemtest")

tasks.register("checkstyleAll", Checkstyle::class) {
    dependsOn(checkstyleMain.get(), checkstyleTest.get(), checkstyleSystemtest.get())
    tasks.named("checkstyleTest").get().mustRunAfter("checkstyleMain")
    tasks.named("checkstyleSystemtest").get().mustRunAfter("checkstyleTest")
}

tasks.register("environmentalChecks", Exec::class) {
    workingDir = file("config/checks/")
    if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
        commandLine("cmd", "/c", "run-checks.bat")
    } else {
        commandLine("sh", "./run-checks.sh")
    }
}

val serveTestReportInBackground = tasks.register("serveTestReportInBackground", JavaExec::class) {
    description = "Creates a background server process for the test report that is to be used by Cypress"

    dependsOn(zipReport, compileJava, processResources, copyCypressConfig, copyMainClasses)
    tasks.named("compileJava").configure { mustRunAfter(zipReport) }
    tasks.named("processResources").configure { mustRunAfter(zipReport) }
    workingDir = file("build/serveTestReport")
    main = mainClass.get()!!
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--config", "./exampleconfig", "--since", "d1", "--view")
    val versionJvmArgs = "-Dversion=" + getRepoSenseVersion()
    jvmArgs = listOf(versionJvmArgs)
//    killDescendants = false
//    waitForPort = 9000
}

val installCypress = tasks.register("installCypress", ExecutePackageManagerTask::class) {
    setWorkingDir("frontend/cypress/")
    setArgs(listOf("ci"))
}

tasks.withType(Copy::class) {
    includeEmptyDirs = true
}

tasks.register("cypress", ExecutePackageManagerTask::class) {
    dependsOn(installCypress, serveTestReportInBackground)

    setWorkingDir(file("frontend/cypress/"))
    setArgs(listOf("run-script", "debug"))
}

tasks.named("serveTestReportInBackground").configure { mustRunAfter(installCypress) }

val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra

jacoco {
    toolVersion = "0.8.7"
}

val jacocoTestReport = tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
        html.destination = file("${buildDir}/jacocoHtml")
    }
    executionData("systemtest", "frontendTest")
}

val coverage = tasks.register<JacocoReport>("coverage").configure {
    sourceDirectories.setFrom(files(sourceSets.main.get().allSource.srcDirs))
    classDirectories.setFrom(files(sourceSets.main.get().output))
    executionData.setFrom(jacocoTestReport.get().executionData)

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

afterEvaluate {
    tasks.named<JacocoReport>("coverage").configure {
        classDirectories.setFrom(classDirectories.files.map {
            fileTree(it) {
                exclude("**/*.jar")
            }
        })
    }
}

fun getRepoSenseVersion(): String {
    var repoSenseVersion = project.property("version") as String
    if (repoSenseVersion == "unspecified") {
        val versionDetails = versionDetails()
        repoSenseVersion = if (versionDetails.commitDistance == 0) {
            versionDetails.lastTag
        } else {
            versionDetails.gitHash
        }
    }
    return repoSenseVersion
}

val syncFrontendPublic = tasks.register("syncFrontendPublic", Sync::class) {
    from("reposense-report")
    into("frontend/public/")
    include("**/*.json")
    includeEmptyDirs = false
    preserve {
        include("index.html")
        include("favicon.ico")
    }
}

val macHotReloadFrontend = tasks.register("macHotReloadFrontend", Exec::class) {
    dependsOn(installFrontend)
    onlyIf {os.isMacOsX()}
    workingDir = file("frontend/")
    commandLine("npm", "run", "serveOpen")
}

val windowsHotReloadFrontend = tasks.register("windowsHotReloadFrontend", Exec::class) {
    dependsOn(installFrontend)
    onlyIf {os.isWindows()}
    setWorkingDir(file("frontend/"))
    commandLine("cmd","/c", "START", "\"hotreload RepoSense frontend\"", "npm", "run", "serveOpen")
}

val linuxHotReloadFrontend = tasks.register("linuxHotReloadFrontend", Exec::class) {
    dependsOn(installFrontend)
    onlyIf {os.isLinux()}
    setWorkingDir(file("frontend/"))
    commandLine("npm", "run", "serveOpen")
}

tasks.register("hotReloadFrontend") {
    dependsOn(syncFrontendPublic)
    finalizedBy(windowsHotReloadFrontend)
    finalizedBy(macHotReloadFrontend)
    finalizedBy(linuxHotReloadFrontend)
}
// End of hot reload Tasks

fun deleteReposAddressDirectory() {
    val REPOS_ADDRESS = "repos"
    val reposDirectory = File(REPOS_ADDRESS)
    reposDirectory.deleteRecursively()
}

defaultTasks("clean", "build", "systemtest", "frontendTest", "coverage")
