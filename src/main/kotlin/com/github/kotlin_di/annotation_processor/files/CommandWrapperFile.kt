package com.github.kotlin_di.annotation_processor.files

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName

class CommandWrapperFile(packageName: String, private val fn: KSFunctionDeclaration) : IFile(packageName, "${fn.simpleName.asString()}Wrapper") {

    private val fnPackageName = fn.packageName.asString()
    private val fnName = fn.simpleName.asString()
    private val wrapperName = "${fnName}Wrapper"
    val wrapper = ClassName(packageName, wrapperName)
    private val props = mutableListOf<PropertySpec.Builder>()

    override fun build(file: FileSpec.Builder): FileSpec {
        val params = fn.parameters
        var args = ""
        return file.apply {
            addImport(fnPackageName, fnName)
            addType(
                TypeSpec.classBuilder(wrapperName).apply {
                    addSuperinterface(ClassName("com.github.kotlin_di.common.command", "Command"))
                    primaryConstructor(
                        FunSpec.constructorBuilder().apply {
                            params.forEachIndexed { i, p ->
                                val pName = p.name!!.getShortName()
                                val pType = p.type.resolve().toTypeName()
                                addParameter(pName, pType)
                                props.add(
                                    PropertySpec.builder(
                                        pName,
                                        pType,
                                        KModifier.PRIVATE
                                    ).initializer(pName)
                                )
                                args += pName
                                if (i != params.size - 1) {
                                    args += ", "
                                }
                            }
                        }.build()
                    )
                    addProperties(props.map { it.build() })
                    addFunction(
                        FunSpec.builder("invoke").apply {
                            addModifiers(KModifier.OVERRIDE)
                            addCode("$fnName($args)")
                        }.build()
                    )
                }.build()
            )
        }.build()
    }
}
