package com.github.kotlin_di.annotation_processor.dependencies

import com.github.kotlin_di.annotation_processor.files.CommandWrapperFile
import com.github.kotlin_di.annotation_processor.files.Files
import com.github.kotlin_di.common.interfaces.Dependency
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class CommandWrapperNew(private val container: Files) :
    Dependency<Pair<KSFunctionDeclaration, String>, CommandWrapperFile> {

    override fun invoke(args: Pair<KSFunctionDeclaration, String>): CommandWrapperFile {
        val (fn, name) = args
        return container.file("${fn.simpleName}Wrapper") { CommandWrapperFile(it, fn, name) }
    }
}
