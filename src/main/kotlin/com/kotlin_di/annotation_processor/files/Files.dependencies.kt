package com.kotlin_di.annotation_processor.files

import com.google.devtools.ksp.processing.KSPLogger
import com.kotlin_di.annotation_processor.dependencies.DTOWrapperNew
import com.kotlin_di.common.command.Command
import com.kotlin_di.ioc.asDependency
import com.kotlin_di.resolve

fun Files.dependencies(mainName: String, logger: KSPLogger) {
    resolve<Command>("IoC.Register", "Files.Main", asDependency { this.file("Main") { MainFile(it, mainName) } })()
    resolve<Command>("IoC.Register", "Files.DTOWrapper", DTOWrapperNew(this))()
    resolve<Command>("IoC.Register", "Files.Logger", asDependency { logger })()
}
