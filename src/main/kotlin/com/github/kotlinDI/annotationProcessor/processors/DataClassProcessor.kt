package com.github.kotlinDI.annotationProcessor.processors

import com.github.kotlinDI.annotationProcessor.files.DTOWrapperFile
import com.github.kotlinDI.annotationProcessor.files.Files
import com.github.kotlinDI.common.annotations.DTO
import com.github.kotlinDI.resolve
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.toClassName

class DataClassProcessor : IProcessor(DTO::class) {

    private inner class Visitor : KSVisitorVoid() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val name = classDeclaration.toClassName()
            val file: DTOWrapperFile = resolve(Files.DTOWrapper, name)
            classDeclaration.getAllProperties().forEach {
                file.addProperty(it)
            }
        }
    }

    override fun processAnnotation(symbols: Sequence<KSAnnotated>) {
        symbols.filter {
            it is KSDeclaration && it.validate()
        }.forEach {
            it.accept(Visitor(), Unit)
        }
    }
}