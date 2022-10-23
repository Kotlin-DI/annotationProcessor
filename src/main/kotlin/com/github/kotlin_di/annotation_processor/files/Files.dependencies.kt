package com.github.kotlin_di.annotation_processor.files

import com.github.kotlin_di.annotation_processor.dependencies.CommandWrapperNew
import com.github.kotlin_di.annotation_processor.dependencies.DTOWrapperNew
import com.github.kotlin_di.common.types.by
import com.github.kotlin_di.ioc.IoC
import com.github.kotlin_di.resolve
import com.google.devtools.ksp.processing.KSPLogger

fun Files.dependencies(mainName: String, logger: KSPLogger) {
    resolve(IoC.REGISTER, Files.Main by { this.file("Main") { MainFile(it, mainName + "Plugin") } })()
    resolve(IoC.REGISTER, Files.Keys by { this.file("Keys") { KeysFile(it, mainName) } })()
    resolve(IoC.REGISTER, Files.DTOWrapper by DTOWrapperNew(this))()
    resolve(IoC.REGISTER, Files.CommandWrapper by CommandWrapperNew(this))()
    resolve(IoC.REGISTER, Files.Logger by { logger })()
}
