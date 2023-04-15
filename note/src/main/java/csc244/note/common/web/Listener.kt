package csc244.note.common.web

import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * @author James Chan
 */
class Listener<T : Any>(
    private val dtoClass: KClass<T>?,
    private val callback: (T) -> Unit
) : Consumer<T> {
    constructor(callback: () -> Unit) : this(null, { callback() }) {
    }

    fun getDtoClass(): KClass<T>? {
        return dtoClass
    }

    override fun accept(dto: T) {
        callback(dto)
    }
}