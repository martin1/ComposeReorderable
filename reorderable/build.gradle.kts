import org.jetbrains.compose.ComposeBuildConfig.composeVersion
import org.jetbrains.compose.compose

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("maven-publish")
    id("signing")
}

group = "org.burnoutcrew.composereorderable"
version = "0.7.4"

kotlin {
    android {
        publishLibraryVariants( "debug","release")
    }
    jvm("desktop")
    sourceSets {
        val commonMain by getting {
            dependencies {
                compileOnly(compose.foundation)
                compileOnly("org.jetbrains.compose.ui:ui-util:${composeVersion}")
            }
        }
    }
}

android {
    compileSdk = rootProject.extra.get("compileSdk") as Int

    defaultConfig {
        minSdk =  rootProject.extra.get("minVersion") as Int
        targetSdk = rootProject.extra.get("targetSdk") as Int
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}
val javadocJar = tasks.register("javadocJar", Jar::class.java) {
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        repositories {
            maven {
                name="oss"
                val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                credentials {
                    username = extra.properties.getOrDefault("ossrh.Username", "") as String
                    password = extra.properties.getOrDefault("ossrh.Password", "") as String
                }
            }
        }
    }
    publications {
        withType<MavenPublication> {
            artifact(javadocJar)
            pom {
                name.set("ComposeReorderable")
                description.set("Reorderable Compose LazyList")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }
                url.set("https://github.com/aclassen/ComposeReorderable")
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/aclassen/ComposeReorderable/issues")
                }
                scm {
                    connection.set("https://github.com/aclassen/ComposeReorderable.git")
                    url.set("https://github.com/aclassen/ComposeReorderable")
                }
                developers {
                    developer {
                        name.set("Andre Claßen")
                        email.set("andreclassen1337@gmail.com")
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications)
}
