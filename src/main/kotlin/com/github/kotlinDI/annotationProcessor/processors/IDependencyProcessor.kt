package com.github.kotlinDI.annotationProcessor.processors

import com.github.kotlinDI.annotationProcessor.files.Files
import com.github.kotlinDI.common.annotations.IDependency
import com.github.kotlinDI.resolve
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

class IDependencyProcessor() : IProcessor(IDependency::class) {

    private inner class Visitor : KSVisitorVoid() {
        @OptIn(KspExperimental::class)
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val fileMain = resolve(Files.Main)
            val fileKeys = resolve(Files.Keys)
            val name = classDeclaration.toClassName()
            val annotation = classDeclaration.getAnnotationsByType(IDependency::class).first()
            val invoke = classDeclaration.getDeclaredFunctions().first { it.simpleName.getShortName() == "invoke" }
            val params = invoke.parameters[0].type.toTypeName()
            val returnType = invoke.returnType?.resolve()
            val key = fileKeys.addKey(annotation.key, params, returnType, classDeclaration.docString)
            fileMain.addDependencyClass(key, name)
        }

        @OptIn(KspExperimental::class)
        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            val fileMain = resolve(Files.Main)
            val fileKeys = resolve(Files.Keys)
            val name = ClassName(function.packageName.asString(), function.simpleName.asString())
            val annotation = function.getAnnotationsByType(IDependency::class).first()
            val params = function.parameters[0].type.toTypeName()
            val returnType = function.returnType?.let { it.resolve() }
            val key = fileKeys.addKey(annotation.key, params, returnType, function.docString)
            fileMain.addDependencyFun(key, name)
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