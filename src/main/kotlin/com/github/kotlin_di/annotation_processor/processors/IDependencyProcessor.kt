package com.github.kotlin_di.annotation_processor.processors

import com.github.kotlin_di.annotation_processor.files.Files
import com.github.kotlin_di.common.annotations.IDependency
import com.github.kotlin_di.resolve
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

class IDependencyProcessor() : IProcessor(IDependency::class) {

    private inner class Visitor : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val fileMain = resolve(Files.Main)
            val fileKeys = resolve(Files.Keys)
            val name = classDeclaration.toClassName()
            val annotation = classDeclaration.annotations.first { it.shortName.asString() == IDependency::class.simpleName }
            val invoke = classDeclaration.getDeclaredFunctions().first { it.simpleName.getShortName() == "invoke" }
            val keyName = annotation.arguments[0].value!! as String
            val params = invoke.parameters[0].type.resolve()
            val returnType = invoke.returnType?.let { it.resolve() }
            val key = fileKeys.addKey(keyName, params, returnType)
            fileMain.addDependencyClass(key, name)
        }

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            val fileMain = resolve(Files.Main)
            val fileKeys = resolve(Files.Keys)
            val name = ClassName(function.packageName.asString(), function.simpleName.asString())
            val annotation = function.annotations.first { it.shortName.asString() == IDependency::class.simpleName }
            val keyName = annotation.arguments[0].value!! as String
            val params = function.parameters[0].type.resolve()
            val returnType = function.returnType?.let { it.resolve() }
            val key = fileKeys.addKey(keyName, params, returnType)
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
