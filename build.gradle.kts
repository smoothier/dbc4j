plugins {
    java
    id("org.jetbrains.qodana") version "0.1.13"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
}

dependencies {
    compileOnly("org.jetbrains:annotations:22.0.0")
    testCompileOnly("org.jetbrains:annotations:22.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}


val reportsDir = file("$buildDir/reports")
val dataDir = file("$buildDir/data")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

qodana {
    saveReport.set(true)
    showReport.set(false)
    dockerContainerName.set("qodana")
    reportPath.set("$reportsDir/codequality/qodana")
    resultsPath.set("$dataDir/codequality/qodana")
}

tasks.register<DefaultTask>("codequality") {
    group = "verification"
    description = """
        Analyses the code quality.
    """.trimIndent()
}
val TaskContainer.codequality: TaskProvider<DefaultTask>
    get() = named<DefaultTask>("codequality")

tasks {
    runInspections {
        dependsOn += test
        dependsOn += assemble
    }

    test {
        useJUnitPlatform() {

        }
        reports.html.outputLocation.set(file("$reportsDir/tests/unit/html"))
        reports.junitXml.outputLocation.set(file("$reportsDir/tests/unit/xml"))
        binaryResultsDirectory.set(file("$dataDir/tests/unit"))
    }

    check {
        dependsOn += codequality
    }

    codequality {
        dependsOn += runInspections
    }
}
