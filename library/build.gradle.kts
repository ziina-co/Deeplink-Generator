import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.maven.publish)
    id("signing")
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(libs.jetbrains.kotlin.stdlib)
    implementation(libs.google.devtools.ksp)

    testImplementation(libs.junit)
}

val groupName = "com.ziina.library"
val artifactName = "deeplinkgenerator"
val versionName = "0.0.1"
group = groupName
version = versionName

mavenPublishing {
    publishToMavenCentral(SonatypeHost.DEFAULT)
    signAllPublications()
    coordinates(groupName, artifactName, versionName)

    pom {
        name.set("Deeplink Generator")
        description.set("A KSP library for generating deeplinks in Android applications")
        inceptionYear.set("2024")
        url.set("https://github.com/ziina-co/Deeplink-Generator")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("bwdude")
                name.set("Anton Dudakov")
                url.set("https://github.com/antondudakov/")
            }
        }
        scm {
            url.set("https://github.com/ziina-co/Deeplink-Generator")
            connection.set("scm:git@github.com:ziina-co/Deeplink-Generator.git")
            developerConnection.set("scm:git:ssh://git@github.com/ziina-co/Deeplink-Generatorr.git")
        }
    }
}