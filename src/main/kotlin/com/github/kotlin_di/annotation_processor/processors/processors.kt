package com.github.kotlin_di.annotation_processor.processors

fun processors(): List<IProcessor> {
    return listOf(
        IDependencyProcessor(),
        ICommandProcessor(),
        DataClassProcessor()
    )
}
