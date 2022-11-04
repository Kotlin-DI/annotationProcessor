import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    java
    `java-library`
    `maven-publish`
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("org.jetbrains.dokka") version "1.6.21"
    id("me.qoomon.git-versioning") version "6.3.0"
}

group = "com.github.kotlin_di"
version = "0.0.0-SNAPSHOT"
gitVersioning.apply {
    refs {
        branch(".+") {
            version = "\${ref}-SNAPSHOT"
        }
        tag("v(?<version>.*)") {
            version = "\${ref.version}"
        }
    }

    // optional fallback configuration in case of no matching ref configuration
    rev {
        version = "\${commit}"
    }
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
    maven("https://jitpack.io")
    mavenLocal()
}

val kotlinVersion: String by project
val kotlinPoetVersion: String by project
val kspVersion: String by project

dependencies {
    implementation("com.github.Kotlin-DI:common:0.0.6")
    implementation("com.github.Kotlin-DI:ioc:0.0.6")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("com.squareup:kotlinpoet-ksp:$kotlinPoetVersion")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kotlinVersion-$kspVersion")
    testImplementation(platform("org.junit:junit-bom:5.9.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

ktlint {
    disabledRules.set(setOf("no-wildcard-imports"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
            jvmTarget = "11"
        }
        dependsOn("ktlintFormat")
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    register<Copy>("copyGitHooks") {
        description = "Copy git hooks from scripts/git-hooks"
        group = "git-hooks"
        from("$rootDir/scripts/git-hooks/") {
            include("**/*.sh")
            rename("(.*).sh", "$1")
        }
        into("$rootDir/.git/hooks")
    }

    register<Exec>("installGitHooks") {
        description = "Installs the pre-commit git hooks from scripts/git-hooks."
        group = "git-hooks"
        onlyIf {
            !org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS)
        }
        dependsOn(named("copyGitHooks"))

        workingDir(rootDir)
        commandLine("chmod")
        args("-R", "+x", ".git/hooks/")

        doLast {
            logger.info("Git hooks installed successfully.")
        }
    }

    register<Delete>("deleteGitHooks") {
        group = "git-hooks"
        description = "Delete the pre-commit git hooks."
        delete(fileTree(".git/hooks/"))
    }

    afterEvaluate {
        tasks["clean"].dependsOn(tasks.named("installGitHooks"))
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            from(components["java"])
        }
    }
}
