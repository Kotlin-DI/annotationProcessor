package com.github.kotlin_di.annotation_processor.files

import com.github.kotlin_di.common.plugins.IPlugin
import com.squareup.kotlinpoet.*

class MainFile(packageName: String, private val className: String) : IFile(packageName, className) {

    private val importList = mutableListOf(
        ClassName("com.github.kotlin_di", "resolve"),
        ClassName("com.github.kotlin_di.common.command", "Command")
    )

    private var loadScript = ""

    fun addDependency(key: String, className: ClassName) {
        importList.add(className)
        loadScript += "resolve<Command>(\"IoC.Register\",\"$key\",${className.simpleName}())()\n"
    }

    override fun build(file: FileSpec.Builder): FileSpec {
        return file.apply {
            importList.forEach {
                addImport(it.packageName, it.simpleName)
            }
            addType(
                TypeSpec.classBuilder(className).apply {
                    addSuperinterface(IPlugin::class)
                    addFunction(
                        FunSpec.builder("load").apply {
                            addModifiers(KModifier.OVERRIDE)
                            addStatement(loadScript)
                        }.build()
                    )
                }.build()
            )
        }.build()
    }
}
