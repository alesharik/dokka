package org.jetbrains.dokka.base.transformers.documentables

import org.jetbrains.dokka.model.*
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.transformers.documentation.PreMergeDocumentableTransformer
import org.jetbrains.dokka.transformers.documentation.sourceSet
import org.jetbrains.dokka.utilities.associateWithNotNull

class IncludeSourcesDocumentableFilterTransformer(val context: DokkaContext) : PreMergeDocumentableTransformer {
    private fun <T> List<T>.filterAllowed(dPackage: DPackage, includes: List<Regex>): List<T> where T : Documentable =
        this.filter {
            val name = "${dPackage.packageName}.${it.name}"
            includes.any { regex -> regex.matches(name) }
        }

    override fun invoke(modules: List<DModule>) =
        modules.mapNotNull(::filterModule)

    private fun filterModule(module: DModule): DModule? {
        val includes = module.sourceSets.associateWith {
            it.includeSources.map(::Regex)
        }
        val packages = module.packages
            .associateWithNotNull { pkg -> includes[sourceSet(pkg)] }
            .mapNotNull { (pkg, includeRegexes) -> filterPackage(pkg, includeRegexes) }

        return when {
            packages == module.packages -> module
            packages.isEmpty() -> null
            else -> module.copy(packages = packages)
        }
    }

    private fun filterPackage(dPackage: DPackage, includes: List<Regex>): DPackage? {
        val skipPackage = includes.isEmpty() || includes.any { regex -> regex.matches(dPackage.packageName) }
        if (skipPackage) {
            return dPackage
        }
        val classlikes = dPackage.classlikes.filterAllowed(dPackage, includes)
        val functions = dPackage.functions.filterAllowed(dPackage, includes)
        val typealiases = dPackage.typealiases.filterAllowed(dPackage, includes)
        val properties = dPackage.properties.filterAllowed(dPackage, includes)
        val filteredChildrenLength = classlikes.size + functions.size + typealiases.size + properties.size
        return when {
            filteredChildrenLength == 0 -> null
            dPackage.children.size == filteredChildrenLength -> dPackage
            else -> dPackage.copy(
                classlikes = classlikes,
                functions = functions,
                typealiases = typealiases,
                properties = properties
            )
        }
    }
}
