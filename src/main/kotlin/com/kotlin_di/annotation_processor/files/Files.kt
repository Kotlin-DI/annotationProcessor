package com.kotlin_di.annotation_processor.files

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies

class Files(private val group: String) {

    private val dict = hashMapOf<String, IFile>()

    fun file(key: String, orElse: (packageName: String) -> IFile): IFile {
        return dict.getOrPut(key) { orElse(group) }
    }

    fun load(codeGenerator: CodeGenerator, dependencies: Dependencies) {
        dict.values.forEach {
            it.load(codeGenerator, dependencies)
        }
    }
}
