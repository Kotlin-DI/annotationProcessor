package com.kotlin_di.annotation_processor.processors

fun processors(): List<IProcessor> {
    return listOf(
        IDependencyProcessor(),
        DataClassProcessor()
    )
}
