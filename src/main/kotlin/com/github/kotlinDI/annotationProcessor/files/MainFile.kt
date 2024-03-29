package com.github.kotlinDI.annotationProcessor.files

import com.github.kotlinDI.common.interfaces.Command
import com.github.kotlinDI.common.plugins.IPlugin
import com.github.kotlinDI.ioc.IoC
import com.github.kotlinDI.resolve
import com.squareup.kotlinpoet.*

class MainFile(packageName: String, private val className: String) : IFile(packageName, className) {

    private val importList = mutableListOf(
        ClassName("com.github.kotlinDI", "resolve"),
        ClassName("com.github.kotlinDI.common.types", "by"),
        Command::class.asClassName(),
        IoC::class.asClassName()
    )

    private var loadScript = ""

    fun addDependencyClass(key: Pair<ClassName, String>, className: ClassName) {
        importList.add(className)
        importList.add(key.first)
        loadScript += "resolve(IoC.REGISTER,${key.first.simpleName}.${key.second} by ${className.simpleName}())()\n"
    }

    fun addDependencyFun(key: Pair<ClassName, String>, fName: ClassName) {
        importList.add(fName)
        importList.add(key.first)
        loadScript += "resolve(IoC.REGISTER,${key.first.simpleName}.${key.second} by ::${fName.simpleName})()\n"
    }

    override fun build(file: FileSpec.Builder): FileSpec {
        return file.apply {
            importList.forEach {
                addImport(it.packageName, it.simpleName)
            }
            addType(
                TypeSpec.classBuilder(className).apply {
                    addSuperinterface(
                        IPlugin::class
                            .asTypeName()
                    )
                    addFunction(
                        FunSpec.builder("load").apply {
                            addModifiers(KModifier.OVERRIDE)
                            addStatement(loadScript)
                        }.build()
                    )
                    addProperty(
                        PropertySpec.builder("version", String::class, KModifier.OVERRIDE)
                            .apply {
                                initializer("\"${resolve(Files.Version)}\"")
                            }.build()
                    )
                }.build()
            )
        }.build()
    }
}