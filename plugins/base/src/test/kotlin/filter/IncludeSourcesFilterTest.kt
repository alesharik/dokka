package filter

import org.jetbrains.dokka.base.testApi.testRunner.BaseAbstractTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class IncludeSourcesFilterTest : BaseAbstractTest() {
    @Test
    fun `two packages with one package regex in includeSources`() {
        val configuration = dokkaConfiguration {
            sourceSets {
                sourceSet {
                    includeSources = setOf("stay.*")
                    sourceRoots = listOf("src/main/kotlin/")
                }
            }
        }

        testInline(
            """
            |/src/main/kotlin/stay/Test.kt
            |package stay
            |
            |fun testFunction() { }
            |
            |class TestClass {}
            |
            |typealias TestAlias = TestClass
            |
            |val TEST = "test"
            |
            |
            |/src/main/kotlin/remove/Test.kt
            |package remove
            |
            |fun testFunction1() { }
            |
            |class TestClass1 {}
            |
            |typealias TestAlias1 = TestClass1
            |
            |val TEST1 = "test"
            |
        """.trimMargin(),
            configuration
        ) {
            preMergeDocumentablesTransformationStage = {
                val packages = it.first().packages
                Assertions.assertEquals(1, packages.size)
                val firstPackage = packages.first()
                Assertions.assertEquals("stay", firstPackage.packageName)
                Assertions.assertTrue(
                    firstPackage.children.size == 4
                )
            }
        }
    }

    @Test
    fun `two packages with one Test_ regex in includeSources`() {
        val configuration = dokkaConfiguration {
            sourceSets {
                sourceSet {
                    includeSources = setOf("stay.Test.*")
                    sourceRoots = listOf("src/main/kotlin/")
                }
            }
        }

        testInline(
            """
            |/src/main/kotlin/stay/Test.kt
            |package stay
            |
            |fun testFunction() { }
            |
            |class TestClass {}
            |
            |typealias TestAlias = TestClass
            |
            |val TEST = "test"
            |
            |
            |/src/main/kotlin/remove/Test.kt
            |package remove
            |
            |fun testFunction1() { }
            |
            |class TestClass1 {}
            |
            |typealias TestAlias1 = TestClass1
            |
            |val TEST1 = "test"
            |
        """.trimMargin(),
            configuration
        ) {
            preMergeDocumentablesTransformationStage = {
                val packages = it.first().packages
                Assertions.assertEquals(1, packages.size)
                val firstPackage = packages.first()
                Assertions.assertEquals("stay", firstPackage.packageName)
                Assertions.assertTrue(
                    firstPackage.children.size == 2
                )
            }
        }
    }



    @Test
    fun `two packages with empty includeSources`() {
        val configuration = dokkaConfiguration {
            sourceSets {
                sourceSet {
                    includeSources = emptySet()
                    sourceRoots = listOf("src/main/kotlin/")
                }
            }
        }

        testInline(
            """
            |/src/main/kotlin/stay/Test.kt
            |package stay
            |
            |fun testFunction() { }
            |
            |class TestClass {}
            |
            |typealias TestAlias = TestClass
            |
            |val TEST = "test"
            |
            |
            |/src/main/kotlin/remove/Test.kt
            |package remove
            |
            |fun testFunction1() { }
            |
            |class TestClass1 {}
            |
            |typealias TestAlias1 = TestClass1
            |
            |val TEST1 = "test"
            |
        """.trimMargin(),
            configuration
        ) {
            preMergeDocumentablesTransformationStage = {
                Assertions.assertEquals(
                    listOf(4, 4),
                    it.first().packages.map { dPackage -> dPackage.children.size }
                )
            }
        }
    }
}
