package com.github.kotlinDI.annotationProcessor.dependencies

import com.github.kotlinDI.annotationProcessor.files.DTOWrapperFile
import com.github.kotlinDI.annotationProcessor.files.Files
import com.github.kotlinDI.common.interfaces.Dependency
import com.squareup.kotlinpoet.ClassName

class DTOWrapperNew(private val container: Files) : Dependency<ClassName, DTOWrapperFile> {

    override fun invoke(args: ClassName): DTOWrapperFile {
        return container.file("${args.simpleName}Wrapper") { DTOWrapperFile(it, args) }
    }
}