package com.github.kotlinDI.annotationProcessor.processors

import com.github.kotlinDI.annotationProcessor.files.Files
import com.github.kotlinDI.common.annotations.ICommand
import com.github.kotlinDI.resolve
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate

class ICommandProcessor : IProcessor(ICommand::class) {

    private inner class Visitor : KSVisitorVoid() {

        @OptIn(KspExperimental::class)
        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            val name = function.getAnnotationsByType(ICommand::class).first().name
            val file = resolve(Files.CommandWrapper, function to name)
            file.wrapper
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