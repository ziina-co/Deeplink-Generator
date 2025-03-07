/*
 * Copyright 2024 Ziina FZ-LLC.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Author: Anton Dudakov
 *
 */

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
val versionName = "0.1.0"
group = groupName
version = versionName

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
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
            developerConnection.set("scm:git:ssh://git@github.com/ziina-co/Deeplink-Generator.git")
        }
    }
}