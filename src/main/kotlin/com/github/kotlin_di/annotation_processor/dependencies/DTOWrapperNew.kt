package com.github.kotlin_di.annotation_processor.dependencies

import com.github.kotlin_di.annotation_processor.files.DTOWrapperFile
import com.github.kotlin_di.annotation_processor.files.Files
import com.github.kotlin_di.annotation_processor.files.IFile
import com.github.kotlin_di.ioc.Dependency
import com.github.kotlin_di.ioc.cast
import com.squareup.kotlinpoet.ClassName

class DTOWrapperNew(private val container: Files) : Dependency {

    override fun invoke(args: Array<out Any>): IFile {
        val className: ClassName = cast(args[0])
        return container.file("${className.simpleName}Wrapper") { DTOWrapperFile(it, className) }
    }
}
