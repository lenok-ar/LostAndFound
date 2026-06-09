package com.example.lostandfound.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.*
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArchitectureTest {

    @Test
    fun `domain layer has no android dependencies`() {
        val domainFiles = Konsist.scopeFromProject()
            .files
            .withPackage("com.example.domain..")

        assertTrue("Domain layer must contain files", domainFiles.isNotEmpty())
        assertTrue(
            "Domain layer must not import Android framework",
            domainFiles.all { file ->
                !file.hasImport { import ->
                    import.name.startsWith("android.") || import.name.startsWith("androidx.")
                }
            }
        )
    }

    @Test
    fun `data layer has no ui dependencies`() {
        val dataFiles = Konsist.scopeFromProject()
            .files
            .withPackage("com.example.data..")

        assertTrue("Data layer must contain files", dataFiles.isNotEmpty())
        val forbiddenUiPackages = listOf(
            "androidx.compose.",
            "androidx.activity.",
            "androidx.navigation.",
            "com.example.add.",
            "com.example.details.",
            "com.example.feed.",
            "com.example.profile."
        )
        assertTrue(
            "Data layer must not import UI components",
            dataFiles.all { file ->
                !file.hasImport { import ->
                    forbiddenUiPackages.any(import.name::startsWith)
                }
            }
        )
    }

    @Test
    fun `feature modules do not depend on each other directly`() {
        val featurePackages = listOf(
            "com.example.feed..",
            "com.example.add..",
            "com.example.details..",
            "com.example.profile..",
            "com.example.auth..",
            "com.example.about..",
            "com.example.assistant.."
        )
        val scope = Konsist.scopeFromProject()

        featurePackages.forEach { sourcePackage ->
            val sourceFiles = scope.files.withPackage(sourcePackage)
            assertTrue("$sourcePackage must contain files", sourceFiles.isNotEmpty())

            featurePackages
                .filterNot { it == sourcePackage }
                .forEach { targetPackage ->
                    sourceFiles.forEach { sourceFile ->
                        val targetPackagePrefix = targetPackage.removeSuffix("..") + "."
                        assertFalse(
                            "${sourceFile.path} must not import $targetPackage",
                            sourceFile.hasImport { import -> import.name.startsWith(targetPackagePrefix) }
                        )
                    }
                }
        }
    }

    @Test
    fun `use cases are in domain layer`() {
        val useCases = Konsist.scopeFromProject()
            .classes()
            .withNameEndingWith("UseCase")

        assertTrue("Project must contain use cases", useCases.isNotEmpty())
        assertTrue(
            "All use cases must reside in the domain layer",
            useCases.all { it.resideInPackage("com.example.domain.usecase..") }
        )
    }

    @Test
    fun `repositories are interfaces in domain and implementations in data`() {
        val repositoryInterfaces = Konsist.scopeFromProject()
            .interfaces()
            .withNameEndingWith("Repository")
        val repositoryImplementations = Konsist.scopeFromProject()
            .classes()
            .withNameEndingWith("Repository")

        assertTrue("Domain must contain repository interfaces",
            repositoryInterfaces.isNotEmpty())
        assertTrue("Data must contain repository implementations",
            repositoryImplementations.isNotEmpty())
        assertTrue(
            "Repository interfaces must reside in domain",
            repositoryInterfaces.all { it.resideInPackage("com.example.domain.repository..") }
        )
        assertTrue(
            "Repository implementations must reside in data",
            repositoryImplementations.all { it.resideInPackage("com.example.data.repository..") }
        )
    }
}
