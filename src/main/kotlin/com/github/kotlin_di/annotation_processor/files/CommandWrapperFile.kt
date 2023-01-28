package com.github.kotlin_di.annotation_processor.files

import com.github.kotlin_di.common.interfaces.Command
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName

class CommandWrapperFile(packageName: String, private val fn: KSFunctionDeclaration, private val name: String) : IFile(packageName, name) {

    private val fnPackageName = fn.packageName.asString()
    private val fnName = fn.simpleName.asString()
    val wrapper = ClassName(packageName, name)
    private val props = mutableListOf<PropertySpec.Builder>()

    override fun build(file: FileSpec.Builder): FileSpec {
        val params = fn.parameters
        var args = ""
        return file.apply {
            addImport(fnPackageName, fnName)
            addType(
                TypeSpec.classBuilder(name).apply {
                    addSuperinterface(Command::class)
                    primaryConstructor(
                        FunSpec.constructorBuilder().apply {
                            params.forEachIndexed { i, p ->
                                val pName = p.name!!.getShortName()
                                val pType = p.type.toTypeName()
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
