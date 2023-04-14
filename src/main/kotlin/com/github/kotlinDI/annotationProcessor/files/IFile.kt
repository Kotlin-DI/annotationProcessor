package com.github.kotlinDI.annotationProcessor.files

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo

abstract class IFile(packageName: String, className: String) {

    private val file = FileSpec.builder(packageName, className)

    abstract fun build(file: FileSpec.Builder): FileSpec

    fun load(codeGenerator: CodeGenerator, dependencies: Dependencies) {
        val fileSpec = build(file)
        fileSpec.writeTo(codeGenerator, dependencies)
    }
}