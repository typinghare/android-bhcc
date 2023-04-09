package csc244.note.common

import java.util.function.Consumer

@FunctionalInterface
abstract class Listener<T>(private val dtoClass: Class<T>) : Consumer<T> {
    fun getDtoClass(): Class<T> {
        return dtoClass
    }
}