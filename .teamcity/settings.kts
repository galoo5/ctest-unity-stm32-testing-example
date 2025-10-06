import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.XmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildFeatures.xmlReport
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2025.07"

project {

    buildType(QemuBuildNTest_2)
    buildType(DeveloperBuildNTest)
    buildType(Stm32f4doscoBuildNTest)
}

object DeveloperBuildNTest : BuildType({
    name = "Developer Build-n-Test"

    artifactRules = """
        build-dev-test/Testing => build-dev-test/Testing
        build-dev-test/.ninja_log
        build-dev-test/CMakeCache.txt
        build-dev-test/junit-log.xml
        build-dev-test/test_sample
    """.trimIndent()

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "CMake generation"
            id = "CMake_generation"
            scriptContent = "cmake --preset dev-test --fresh"
        }
        script {
            name = "CMake build"
            id = "CMake_build"
            scriptContent = "cmake --build ./build-dev-test"
        }
        script {
            name = "Developer CTest run"
            id = "Developer_CTest_run"
            scriptContent = "ctest --preset dev-test-run --extra-verbose --output-junit junit-log.xml"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
        xmlReport {
            reportType = XmlReport.XmlReportType.JUNIT
            rules = "build-dev-test/junit-log.xml"
            verbose = true
        }
    }

    requirements {
        matches("teamcity.agent.jvm.os.family", "Linux")
    }
})

object QemuBuildNTest_2 : BuildType({
    name = "Qemu Build-n-Test"

    artifactRules = """
        build-arm-test/Testing => build-arm-test/Testing
        build-arm-test/.ninja_log
        build-arm-test/CMakeCache.txt
        build-arm-test/ctest.map
        build-arm-test/junit-log.xml
        build-arm-test/test_sample.elf
    """.trimIndent()

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "CMake generation"
            id = "CMake_generation"
            scriptContent = "cmake --preset arm-test --fresh"
        }
        script {
            name = "CMake build"
            id = "CMake_build"
            scriptContent = "cmake --build ./build-arm-test"
        }
        script {
            name = "Developer CTest run"
            id = "Developer_CTest_run"
            scriptContent = "ctest --preset arm-test-run --extra-verbose --output-junit junit-log.xml -E unity_sample_test_hw"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
        xmlReport {
            reportType = XmlReport.XmlReportType.JUNIT
            rules = "build-arm-test/junit-log.xml"
            verbose = true
        }
    }

    requirements {
        matches("teamcity.agent.jvm.os.family", "Linux")
    }
})

object Stm32f4doscoBuildNTest : BuildType({
    name = "STM32F4-DOSCO Build-n-Test"

    artifactRules = """
        build-arm-test/Testing => build-arm-test/Testing
        build-arm-test/.ninja_log
        build-arm-test/CMakeCache.txt
        build-arm-test/ctest.map
        build-arm-test/junit-log.xml
        build-arm-test/test_sample.elf
    """.trimIndent()

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "CMake generation"
            id = "CMake_generation"
            scriptContent = "cmake --preset arm-test --fresh"
        }
        script {
            name = "CMake build"
            id = "CMake_build"
            scriptContent = "cmake --build ./build-arm-test"
        }
        script {
            name = "Developer CTest run"
            id = "Developer_CTest_run"
            scriptContent = "ctest --preset arm-test-run --extra-verbose --output-junit junit-log.xml -R unity_sample_test_hw"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
        xmlReport {
            reportType = XmlReport.XmlReportType.JUNIT
            rules = "build-arm-test/junit-log.xml"
            verbose = true
        }
    }

    requirements {
        matches("teamcity.agent.jvm.os.family", "Linux")
    }
})
