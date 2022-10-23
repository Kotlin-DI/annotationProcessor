package com.github.kotlin_di.annotation_processor.dependencies

import com.github.kotlin_di.annotation_processor.files.CommandWrapperFile
import com.github.kotlin_di.annotation_processor.files.Files
import com.github.kotlin_di.common.types.Dependency
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class CommandWrapperNew(private val container: Files) : Dependency<KSFunctionDeclaration, CommandWrapperFile> {

    override fun invoke(fn: KSFunctionDeclaration): CommandWrapperFile {
        return container.file("${fn.simpleName}Wrapper") { CommandWrapperFile(it, fn) }
    }
}
