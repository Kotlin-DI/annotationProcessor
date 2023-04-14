package com.github.kotlinDI.annotationProcessor

import com.github.kotlinDI.annotationProcessor.files.Files
import com.github.kotlinDI.annotationProcessor.files.dependencies
import com.github.kotlinDI.annotationProcessor.processors.IProcessor
import com.github.kotlinDI.annotationProcessor.processors.processors
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*

class Processor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {

    lateinit var dependencies: Dependencies
    private val files: Files
    private val processors: List<IProcessor>

    init {
        println(options)
        val group = options["project.group"]!!
        val name = options["project.name"]!!
        val version = options["project.version"]!!
        val keysName = options["keysFile"] ?: "Keys"
        val pluginName = options["pluginFile"] ?: "Plugin"
        files = Files("$group.$name")
        files.dependencies(keysName, pluginName, version, logger)
        processors = processors()
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray())

        for (p in processors) {
            p.process(resolver)
        }

        return emptyList()
    }

    override fun finish() {
        files.load(codeGenerator, dependencies)
    }
}