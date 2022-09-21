package com.kotlin_di.annotation_processor.processors

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import kotlin.reflect.KClass

abstract class IProcessor(private val annotation: KClass<out Annotation>) {

    abstract fun processAnnotation(symbols: Sequence<KSAnnotated>)

    fun process(resolver: Resolver) {
        val symbols = resolver.getSymbolsWithAnnotation(annotation.qualifiedName!!)
        if (!symbols.iterator().hasNext()) return
        processAnnotation(symbols)
    }
}
