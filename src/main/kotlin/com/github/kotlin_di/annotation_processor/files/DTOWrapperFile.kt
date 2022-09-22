package com.github.kotlin_di.annotation_processor.files

import com.github.kotlin_di.common.annotations.Transform
import com.github.kotlin_di.common.annotations.Validate
import com.github.kotlin_di.common.`object`.UObject
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName

class DTOWrapperFile(packageName: String, private val className: ClassName) : IFile(packageName, "${className.simpleName}Wrapper") {

    private val properties: MutableList<PropertySpec.Builder> = mutableListOf()
    private val methods: MutableList<FunSpec.Builder> = mutableListOf()

    private fun parseOptions(options: List<*>): String {
        return if (options.isNotEmpty()) {
            var opts = "mapOf<String, String>("
            options.forEach {

                if (it !== null && (it as String).isNotEmpty()) {
                    val pair = it.split(':').toTypedArray()
                    opts += "\"${pair[0]}\" to \"${pair[1]}\","
                }
            }
            opts += ")"
            opts
        } else {
            ""
        }
    }

    private fun validation(annotations: List<KSAnnotation>, field: String, nullable: Boolean): String {
        var body = "val err: MutableList<ValidationError> = mutableListOf() \n"

        annotations.forEach { ann ->
            val strategy = ann.arguments[0].value as String
            val options = ann.arguments[1].value as List<*>
            val opt = parseOptions(options)
            body += "resolve<ValidationError?>(\"$strategy\", data, $opt).also {\n    if (it !== null) { err.add(it) }\n}\n"
        }

        if (nullable) {
            body += "if (resolve(\"validation.optional\", this.obj, \"$field\")) return; \n"
        }

        body += "if (err.isNotEmpty()) {\n" +
            "throw ValidationError(\"$field field validation failed\", err)\n" +
            "}"
        return body
    }

    private fun transformFrom(ann: KSAnnotation?, type: String): String {
        return if (ann !== null) {
            val strategy = ann.arguments[0].value as String
            val options = ann.arguments[1].value as List<String>
            val opt = parseOptions(options)
            "resolve(\"transform.from\", \"${strategy}\", value, $type::class, this.obj, $opt)\n"
        } else {
            "resolve(\"transform.from\", \"basic\", value, $type::class, this.obj)\n"
        }
    }

    private fun transformTo(ann: KSAnnotation?, field: String, typeName: TypeName, type: String): String {
        return if (ann !== null) {
            val strategy = ann.arguments[0].value as String
            val options = ann.arguments[1].value as List<String>
            val opt = parseOptions(options)
            "var data: $typeName = resolve(\"transform.to\", \"${strategy}\", \"$field\", $type::class, this.obj, $opt)\n"
        } else {
            "var data: $typeName = resolve(\"transform.to\", \"basic\", \"$field\", $type::class, this.obj)\n"
        }
    }
    fun addProperty(prop: KSPropertyDeclaration) {
        val name = prop.simpleName.asString()
        val typeName = prop.type.toTypeName()
        val annotations = prop.annotations
        val transform = annotations.find {
            it.shortName.asString() == Transform::class.simpleName.toString()
        }

        val typeSimpleName = prop.type.toString()
        val validations = annotations.filter {
            it.shortName.asString() == Validate::class.simpleName.toString()
        }.toList()

        if (validations.isNotEmpty()) {
            methods.add(
                FunSpec.builder("${name}FieldValidator",).apply {
                    addModifiers(KModifier.PRIVATE)
                    addParameter("data", typeName)
                    addCode(validation(validations, name, typeName.isNullable))
                }
            )
        }

        properties.add(
            PropertySpec.builder(name, typeName, KModifier.OVERRIDE).apply {
                getter(
                    FunSpec.getterBuilder().apply {
                        addCode(transformTo(transform, name, typeName, typeSimpleName))
                        if (validations.isNotEmpty()) {
                            addCode("${name}FieldValidator(data) \n")
                        }
                        addCode("return data")
                    }.build()
                )
                if (prop.isMutable) {
                    mutable(true)
                    setter(
                        FunSpec.setterBuilder().apply {
                            addParameter("value", typeName)

                            if (validations.isNotEmpty()) {
                                addCode("${name}FieldValidator(value) \n")
                            }
                            addCode("this.obj[\"$name\"] = ${transformFrom(transform, typeSimpleName)}")
                        }.build()
                    )
                }
            }
        )
    }

    override fun build(file: FileSpec.Builder): FileSpec {
        return file.apply {
            addImport("com.github.kotlin_di", "resolve")
            addImport("com.github.kotlin_di.common.validation", "ValidationError")
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
