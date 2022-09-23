package com.github.kotlin_di.annotation_processor.files

import com.github.kotlin_di.annotation_processor.dependencies.DTOWrapperNew
import com.github.kotlin_di.common.command.Command
import com.github.kotlin_di.ioc.asDependency
import com.github.kotlin_di.resolve
import com.google.devtools.ksp.processing.KSPLogger

fun Files.dependencies(mainName: String, logger: KSPLogger) {
    resolve<Command>("IoC.Register", "Files.Main", asDependency { this.file("Main") { MainFile(it, mainName + "Plugin") } })()
    resolve<Command>("IoC.Register", "Files.Keys", asDependency { this.file("Keys") { KeysFile(it, mainName) } })()
    resolve<Command>("IoC.Register", "Files.DTOWrapper", DTOWrapperNew(this))()
    resolve<Command>("IoC.Register", "Files.Logger", asDependency { logger })()
}
