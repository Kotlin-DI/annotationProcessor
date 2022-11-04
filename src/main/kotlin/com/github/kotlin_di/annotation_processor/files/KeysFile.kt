package com.github.kotlin_di.annotation_processor.files

import com.github.kotlin_di.common.plugins.KeyDefinition
import com.github.kotlin_di.common.types.Key
import com.github.kotlin_di.resolve
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import java.util.*

class KeysFile(private val packageName: String, private val className: String) : IFile(packageName, className) {
    val name = ClassName(packageName, className)
    private val keys = mutableListOf<PropertySpec.Builder>()
    val keysList = mutableListOf<String>()

    fun addKey(key: String, params: KSType, returns: KSType?, doc: String?): Pair<ClassName, String> {
        val r = if (returns == null) {
            Unit::class.asTypeName()
        } else {
            returns.toTypeName()
        }
        val name = key
            .uppercase(Locale.getDefault())
            .replace(" ", "_")
            .replace(".", "_")

        keysList.add(name)
        keys.add(
            PropertySpec.builder(
                name,
                Key::class.asClassName().parameterizedBy(
                    params.toTypeName(),
                    r
                )
            ).apply {
                mutable(false)
                val t = Key::class.asClassName()
                initializer(CodeBlock.of("${t.simpleName}(\"$key\")"))
                if (doc != null) {
                    addKdoc(doc)
                }
            }
        )
        return ClassName(packageName, className) to name
    }

    override fun build(file: FileSpec.Builder): FileSpec {
        return file.apply {
            addImport(Key::class, "")
            addType(
                TypeSpec.objectBuilder(className).apply {
                    addSuperinterface(KeyDefinition::class)
                    addProperties(
                        keys.map {
                            it.build()
                        }
                    )
                    addProperty(
                        PropertySpec.builder("version", String::class, KModifier.OVERRIDE)
                            .apply {
                                initializer("\"${resolve(Files.Version)}\"")
                            }.build()
                    )
                    addFunction(
                        FunSpec.builder("keys").apply {
                            addModifiers(KModifier.OVERRIDE)
                            var args = ""
                            keysList.forEachIndexed { index, s ->
                                args += s
                                if (index != keysList.size - 1) {
                                    args += ", "
                                }
                            }
                            addCode("return listOf($args)")
                        }.build()
                    )
                }.build()
            )
        }.build()
    }
}
