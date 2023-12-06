import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure

plugins {
    id("application")
    id("checkstyle")
    id("idea")
    id("jacoco")
    id("java")
    id("com.github.johnrengelman.shadow") version("7.1.2")
    id("com.liferay.node") version("7.2.18")
    id("com.github.psxpaul.execfork") version("0.2.0")
    id("com.palantir.git-version") version("0.13.0")
}

val os = DefaultNativePlatform.getCurrentOperatingSystem()

application {
    mainClassName = "reposense.RepoSense"
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

// Use either "getByName" or "create"
configurations {
    create("systemtestImplementation").extendsFrom(getByName("testImplementation"))
    create("systemtestRuntime").extendsFrom(getByName("testRuntimeClasspath"))
}

dependencies {
    val jUnitVersion = "5.8.2"
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
    create("systemtest") {
        compileClasspath += main.get().output + test.get().output
        runtimeClasspath += main.get().output + test.get().output
        java.srcDir(file("src/systemtest/java"))
        resources.srcDir(file("src/systemtest/resources"))
    }
}

val installFrontend = tasks.register<com.liferay.gradle.plugins.node.tasks.ExecutePackageManagerTask>("installFrontend") {
    setWorkingDir("frontend/")
    setArgs(listOf("ci"))
}

val buildFrontend = tasks.register<com.liferay.gradle.plugins.node.tasks.ExecutePackageManagerTask>("buildFrontend") {
    dependsOn(installFrontend)
    setWorkingDir("frontend/")
    setArgs(listOf("run", "devbuild"))
}

val zipReport = tasks.register<Zip>("zipReport") {
    dependsOn(buildFrontend)
    from("frontend/build/")
    archiveBaseName.set("templateZip")
    destinationDirectory.set(file("src/main/resources"))
}

val copyCypressConfig = tasks.register<Copy>("copyCypressConfig") {
    description = "Copies the config files used by the backend to generate the test report for Cypress testing into an isolated working directory"
    from("frontend/cypress/config")
    into("build/serveTestReport/exampleconfig")
}

val copyMainClasses = tasks.register<Copy>("copyMainClasses") {
    description = "Copies the backend classes used to generate the test report for Cypress testing into an isolated working directory"
    dependsOn(tasks.named("classes"))
    from("build/classes/java/main")
    into("build/serveTestReport/java/main")
}

val compileJava = tasks.getByName("compileJava")

tasks.named<Copy>("processSystemtestResources") { // removed the .configure from here
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.getByName("run").dependsOn(zipReport);

tasks.named<JavaExec>("run") {
    //the second arguments indicates the default value associated with the property.
    tasks.getByName("compileJava").mustRunAfter(zipReport)
    args = System.getProperty("args", "").split(" ")
    systemProperty("version", getRepoSenseVersion())
}

checkstyle {
    toolVersion = "9.3"
    configDirectory.set(file("${rootProject.projectDir}/config/checkstyle"))
}

idea {
    module {
        sourceSets.getByName("systemtest").allSource.srcDirs.forEach {
            srcDir: File -> testSourceDirs.add(srcDir)
        }
    }
}

tasks.named<Test>("test") {
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

tasks.getByName("shadowJar").dependsOn(zipReport);

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    tasks.getByName("compileJava").mustRunAfter(zipReport)
    tasks.getByName("processResources").mustRunAfter(zipReport)
    archiveFileName.set("RepoSense.jar")
    destinationDirectory.set(file("${buildDir}/jar/"))

    manifest {
        attributes("Implementation-Version" to getRepoSenseVersion())
    }
}

tasks.register<com.liferay.gradle.plugins.node.tasks.ExecutePackageManagerTask>("lintFrontend") {
    dependsOn(installFrontend)
    setWorkingDir("frontend/")
    setArgs(listOf("run", "lint"))
}

val checkstyleMain = tasks.getByName("checkstyleMain")
val checkstyleTest = tasks.getByName("checkstyleTest")
val checkstyleSystemtest = tasks.getByName("checkstyleSystemtest")

tasks.register<Checkstyle>("checkstyleAll") {
    dependsOn(checkstyleMain, checkstyleTest, checkstyleSystemtest)
    tasks.getByName("checkstyleTest").mustRunAfter("checkstyleMain")
    tasks.getByName("checkstyleSystemtest").mustRunAfter("checkstyleTest")
}

tasks.register<Exec>("environmentalChecks") {
    setWorkingDir("config/checks/")
    if (os.isWindows()){
        commandLine("cmd", "/c", "run-checks.bat")
    } else {
        commandLine("sh", "./run-checks.sh")
    }
}


val systemtest = tasks.register<Test>("systemtest") {
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

val processResources = tasks.getByName("processResources")

val serveTestReportInBackground = tasks.register<com.github.psxpaul.task.JavaExecFork>("serveTestReportInBackground") {
    description = "Creates a background server process for the test report that is to be used by Cypress"
    dependsOn(zipReport, compileJava, processResources, copyCypressConfig, copyMainClasses)
    compileJava.mustRunAfter(zipReport)
    processResources.mustRunAfter(zipReport)
    workingDir = file("build/serveTestReport")
    main = application.mainClassName
    classpath = sourceSets.getByName("main").runtimeClasspath
    args = listOf("--config", "./exampleconfig", "--since", "d1", "--view").toMutableList()
    val versionJvmArgs = "-Dversion=" + getRepoSenseVersion()
    jvmArgs = listOf(versionJvmArgs)
    // killDescendants = false // Kills descendants of started process using methods only found in Java 9 and beyond.
    // Above flag is set to true by default but is incompatible with Java 8. It should be removed from this file if we fully migrate to Java 11.
    waitForPort = 9000
    timeout = 120
}

val installCypress = tasks.register<com.liferay.gradle.plugins.node.tasks.ExecutePackageManagerTask>("installCypress") {
    setWorkingDir("frontend/cypress/")
    setArgs(listOf("ci"))
}

tasks.register<com.liferay.gradle.plugins.node.tasks.ExecutePackageManagerTask>("cypress") {
    dependsOn(installCypress, serveTestReportInBackground)
    tasks.getByName("serveTestReportInBackground").mustRunAfter(installCypress)

    setWorkingDir("frontend/cypress/")
    setArgs(listOf("run-script", "debug"))
}

val frontendTest = tasks.register<com.liferay.gradle.plugins.node.tasks.ExecutePackageManagerTask>("frontendTest") {
    dependsOn(installCypress, serveTestReportInBackground)
    tasks.getByName("serveTestReportInBackground").mustRunAfter(installCypress)

    setWorkingDir("frontend/cypress/")
    setArgs(listOf("run-script", "tests"))

    // Run tests in CI without slow motion
    if (project.hasProperty("ci")) {
        setArgs(listOf("run-script", "ci"))
    }
}

tasks.withType<Copy> {
    includeEmptyDirs = true
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
        html.destination = file("${buildDir}/jacocoHtml")
    }

    executionData(systemtest, frontendTest)
}

tasks.register<JacocoReport>("coverage")

tasks.getByName<JacocoReport>("coverage") {
    sourceDirectories.from(files(sourceSets.getByName("main").allSource.srcDirs))
    classDirectories.from(files(sourceSets.getByName("main").output))
    executionData.from(files(tasks.getByName("jacocoTestReport").outputs.files))

    afterEvaluate {
        classDirectories.from(classDirectories.files.map {
            fileTree(it) {
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
    val repoSenseVersion: String = project.findProperty("version")?.toString() ?: ""
    if (repoSenseVersion == "unspecified") {
        val versionDetails: Closure<VersionDetails> by extra
        if (versionDetails().commitDistance == 0) {
            return versionDetails().lastTag
        } else {
            return versionDetails().gitHash
        }
    }
    return repoSenseVersion
}

val syncFrontendPublic = tasks.register<Sync>("syncFrontendPublic") {
    from("reposense-report")
    into("frontend/public/")
    include("**/*.json")
    includeEmptyDirs = false
    preserve {
        include("index.html")
        include("favicon.ico")
    }
}

val macHotReloadFrontend = tasks.register<Exec>("macHotReloadFrontend") {
    dependsOn(installFrontend)
    onlyIf {os.isMacOsX()}
    setWorkingDir("frontend/")
    commandLine("npm", "run", "serveOpen")
}

val windowsHotReloadFrontend = tasks.register<Exec>("windowsHotReloadFrontend") {
    dependsOn(installFrontend)
    onlyIf {os.isWindows()}
    workingDir("frontend/")
    commandLine("cmd", "/c", "START", "'hotreload RepoSense frontend'", "npm", "run", "serveOpen")
}

val linuxHotReloadFrontend = tasks.register<Exec>("linuxHotReloadFrontend") {
    dependsOn(installFrontend)
    onlyIf {os.isLinux()}
    setWorkingDir("frontend/")
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
    val REPOS_ADDRESS : String = "repos"
    val reposDirectory = File(REPOS_ADDRESS)
    reposDirectory.deleteRecursively()
}

defaultTasks("clean", "build", "systemtest", "frontendTest", "coverage")
