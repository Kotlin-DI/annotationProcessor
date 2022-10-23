package com.github.kotlin_di.annotation_processor.dependencies

import com.github.kotlin_di.annotation_processor.files.DTOWrapperFile
import com.github.kotlin_di.annotation_processor.files.Files
import com.github.kotlin_di.common.types.Dependency
import com.squareup.kotlinpoet.ClassName

class DTOWrapperNew(private val container: Files) : Dependency<ClassName, DTOWrapperFile> {

    override fun invoke(className: ClassName): DTOWrapperFile {
        return container.file("${className.simpleName}Wrapper") { DTOWrapperFile(it, className) }
    }
}
