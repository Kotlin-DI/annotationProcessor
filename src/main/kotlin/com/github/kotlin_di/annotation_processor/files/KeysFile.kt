package com.github.kotlin_di.annotation_processor.files

import com.github.kotlin_di.common.types.Key
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import java.util.*

class KeysFile(private val packageName: String, private val className: String) : IFile(packageName, className) {

    private val keys = mutableListOf<PropertySpec.Builder>()

    fun addKey(key: String, params: KSType, returns: KSType?): Pair<ClassName, String> {
        val r = if (returns == null) {
            Unit::class.asTypeName()
        } else {
            returns.toTypeName()
        }
        val name = key
            .uppercase(Locale.getDefault())
            .replace(" ", "_")
            .replace(".", "_")
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
            }
        )
        return ClassName(packageName, className) to name
    }

    override fun build(file: FileSpec.Builder): FileSpec {
        return file.apply {
            addImport(Key::class, "")
            addType(
                TypeSpec.objectBuilder(className).apply {
                    addProperties(
                        keys.map {
                            it.build()
                        }
                    )
                }.build()
            )
        }.build()
    }
}
