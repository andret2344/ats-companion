/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	java
	idea
	jacoco
	id("org.jetbrains.intellij") version "1.13.3"
	id("org.barfuin.gradle.jacocolog") version "3.1.0"
	id("org.jetbrains.kotlin.jvm") version "1.8.22"
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
	version.set("2023.1")
	plugins.add("java")
	updateSinceUntilBuild.set(false)
	pluginName.set("${project.name}-${project.version}")
}

repositories {
	mavenCentral()
}

dependencies {
	testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks {
	withType<JavaCompile> {
		sourceCompatibility = "17"
		targetCompatibility = "17"
	}

	withType<KotlinCompile> {
		kotlinOptions.jvmTarget = "17"
	}

	patchPluginXml {
		version.set("${project.version}")
		sinceBuild.set("231")
	}

	test {
		finalizedBy(jacocoAggregatedReport, jacocoTestCoverageVerification)
	}

	jacocoTestCoverageVerification {
		violationRules {
			rule {
				limit {
					minimum = BigDecimal("0.9")
				}
			}
		}
	}
}
