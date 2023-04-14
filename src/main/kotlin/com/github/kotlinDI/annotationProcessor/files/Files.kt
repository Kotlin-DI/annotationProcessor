package com.github.kotlinDI.annotationProcessor.files

import com.github.kotlinDI.common.types.Key
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
        val CommandWrapper = Key<Pair<KSFunctionDeclaration, String>, CommandWrapperFile>("Files.CommandWrapper")
        val Logger = Key<Unit, KSPLogger>("Files.Logger")
        val Version = Key<Unit, String>("Files.Version")
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