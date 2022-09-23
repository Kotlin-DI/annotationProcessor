package com.github.kotlin_di.annotation_processor.processors

import com.github.kotlin_di.annotation_processor.files.KeysFile
import com.github.kotlin_di.annotation_processor.files.MainFile
import com.github.kotlin_di.common.annotations.IDependency
import com.github.kotlin_di.resolve
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.toClassName

class IDependencyProcessor() : IProcessor(IDependency::class) {

    private inner class Visitor : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val fileMain = resolve<MainFile>("Files.Main")
            val fileKeys = resolve<KeysFile>("Files.Keys")
            val name = classDeclaration.toClassName()
            val annotation = classDeclaration.annotations.first { it.shortName.asString() == IDependency::class.simpleName }
            val key = annotation.arguments[0].value!! as String
            val returns = annotation.arguments[1].value!! as KSType
            fileMain.addDependency(key, name)
            fileKeys.addKey(key, returns.toClassName())
        }

//        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
//            val file = resolve<MainFile>("Files.Main")
//            function
//        }
    }

    override fun processAnnotation(symbols: Sequence<KSAnnotated>) {
        symbols.filter {
            it is KSDeclaration && it.validate()
        }.forEach {
            it.accept(Visitor(), Unit)
        }
    }
}
