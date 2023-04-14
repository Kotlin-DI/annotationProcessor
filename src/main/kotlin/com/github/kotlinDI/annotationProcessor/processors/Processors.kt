package com.github.kotlinDI.annotationProcessor.processors

fun processors(): List<IProcessor> {
    return listOf(
        IDependencyProcessor(),
        ICommandProcessor(),
        DataClassProcessor()
    )
}