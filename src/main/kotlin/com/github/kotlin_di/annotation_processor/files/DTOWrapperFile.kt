package com.github.kotlin_di.annotation_processor.files

import com.github.kotlin_di.common.interfaces.UObject
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName

class DTOWrapperFile(packageName: String, private val className: ClassName) : IFile(packageName, "${className.simpleName}Wrapper") {

    private val properties: MutableList<PropertySpec.Builder> = mutableListOf()
    private val methods: MutableList<FunSpec.Builder> = mutableListOf()
    fun addProperty(prop: KSPropertyDeclaration) {
        val name = prop.simpleName.asString()
        val typeName = prop.type.toTypeName()
        val annotations = prop.annotations

        properties.add(
            PropertySpec.builder(name, typeName, KModifier.OVERRIDE).apply {
                getter(
                    FunSpec.getterBuilder().apply {
                        addCode("return data")
                    }.build()
                )
                if (prop.isMutable) {
                    mutable(true)
                    setter(
                        FunSpec.setterBuilder().apply {
                            addParameter("value", typeName)
                        }.build()
                    )
                }
            }
        )
    }

    override fun build(file: FileSpec.Builder): FileSpec {
        return file.apply {
            addImport("com.github.kotlin_di", "resolve")
            addType(
                TypeSpec.classBuilder("${className.simpleName}Wrapper").apply {
                    primaryConstructor(
                        FunSpec.constructorBuilder().apply {
                            addParameter("obj", UObject::class)
                        }.build()
                    )
                    addProperty(PropertySpec.builder("obj", UObject::class, KModifier.PRIVATE).initializer("obj").build())
                    addSuperinterface(className)
                    methods.forEach {
                        addFunction(it.build())
                    }
                    properties.forEach {
                        addProperty(it.build())
                    }
                }.build()
            )
        }.build()
    }
}
