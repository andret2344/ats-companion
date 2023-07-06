/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

plugins {
	java
	idea
	jacoco
	id("org.jetbrains.intellij") version "1.14.2"
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
	testImplementation(group = "org.assertj", name = "assertj-core", version = "3.24.2")
}

tasks {
	withType<JavaCompile> {
		sourceCompatibility = "17"
		targetCompatibility = "17"
	}

	test {
		finalizedBy(jacocoTestReport)
	}

	withType<JacocoReport> {
		dependsOn(test)

		reports {
			html.required = true
			xml.required = true
		}
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

	patchPluginXml {
		version.set("${project.version}")
		sinceBuild.set("231")
	}
}
