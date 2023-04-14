package com.github.kotlinDI.annotationProcessor.dependencies

import com.github.kotlinDI.annotationProcessor.files.CommandWrapperFile
import com.github.kotlinDI.annotationProcessor.files.Files
import com.github.kotlinDI.common.interfaces.Dependency
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class CommandWrapperNew(private val container: Files) :
    Dependency<Pair<KSFunctionDeclaration, String>, CommandWrapperFile> {

    override fun invoke(args: Pair<KSFunctionDeclaration, String>): CommandWrapperFile {
        val (fn, name) = args
        return container.file("${fn.simpleName}Wrapper") { CommandWrapperFile(it, fn, name) }
    }
}