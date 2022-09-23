package com.github.kotlin_di.annotation_processor.files

import com.github.kotlin_di.ioc.Key
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.util.*

class KeysFile(packageName: String, private val className: String) : IFile(packageName, className) {
    private val importList = mutableListOf<ClassName>()

    private val keys = mutableListOf<Pair<String, ClassName>>()

    fun addKey(key: String, returnType: ClassName) {
        keys.add(key to returnType)
    }

    override fun build(file: FileSpec.Builder): FileSpec {
        return file.apply {
            importList.forEach {
                addImport(it.packageName, it.simpleName)
            }
            addType(
                TypeSpec.classBuilder(className).apply {
                    addModifiers(KModifier.SEALED)
                    addSuperinterface(Key::class.asClassName().parameterizedBy(TypeVariableName("R")))
                    addTypeVariable(TypeVariableName("R"))
                    primaryConstructor(
                        FunSpec.constructorBuilder().apply {
                            addParameter(
                                "_name",
                                String::class.asTypeName()
                            )
                        }.build()
                    )
                    addProperty(PropertySpec.builder("_name", String::class.asTypeName(), KModifier.OVERRIDE).initializer("_name").build())
                    keys.forEach {
                        addType(
                            TypeSpec.objectBuilder(
                                it.first
                                    .uppercase(Locale.getDefault())
                                    .replace(" ", "_")
                                    .replace(".", "_")
                            ).apply {
                                superclass(ClassName(packageName, className).parameterizedBy(it.second))
                                addSuperclassConstructorParameter("\"${it.first}\"")
                            }.build()
                        )
                    }
                }.build()
            )
        }.build()
    }
}
