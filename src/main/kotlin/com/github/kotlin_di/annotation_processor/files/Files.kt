package com.github.kotlin_di.annotation_processor.files

import com.github.kotlin_di.common.types.Key
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName

class Files(private val group: String) {

    companion object {
        val Main = Key<Unit, MainFile>("Files.Main")
        val Keys = Key<Unit, KeysFile>("Files.Keys")
        val DTOWrapper = Key<ClassName, DTOWrapperFile>("Files.DTOWrapper")
        val CommandWrapper = Key<KSFunctionDeclaration, CommandWrapperFile>("Files.CommandWrapper")
        val Logger = Key<Unit, KSPLogger>("Files.Logger")
    }

    private val dict = hashMapOf<String, IFile>()

    fun <F : IFile> file(key: String, orElse: (packageName: String) -> F): F {
        return dict.getOrPut(key) { orElse(group) } as F
    }

    fun load(codeGenerator: CodeGenerator, dependencies: Dependencies) {
        dict.values.forEach {
            it.load(codeGenerator, dependencies)
        }
    }
}
