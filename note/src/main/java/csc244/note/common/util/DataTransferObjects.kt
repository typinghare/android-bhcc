package csc244.note.common.util

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility

/**
 * A helper class for data transfer objects (DTOs).
 * @author James Chan
 */
class DataTransferObjects {
    companion object {
        /**
         * Creates a DTO instance.
         */
        private fun <T : Any> createDto(dtoClass: KClass<T>): T {
            return dtoClass.java.getConstructor().newInstance()
        }

        /**
         * Creates a DTO instance with properties set.
         */
        fun <T : Any> createDto(dtoClass: KClass<T>, map: Map<String, *>): T {
            val dto = createDto(dtoClass)
            mapIn(dto as Any, map)

            return dto
        }

        /**
         * Returns the names of all properties of a DTO class.
         */
        fun <T> getProperties(dtoClass: Class<T>): List<String> {
            return dtoClass.declaredFields.mapNotNull { it.name }
        }

        /**
         * Sets the value of a mutable, public property on a DTO object.
         * @param dto The object for which to set the property.
         * @param name The name of the property to set.
         * @param value The new value to assign to the property.
         */
        private fun setProperty(dto: Any, name: String, value: Any?) {
            val dtoClass = dto::class
            val property =
                dtoClass.memberProperties.find { it.name == name } as? KMutableProperty<*>
            property?.let { mutableProp ->
                if (mutableProp.visibility == KVisibility.PUBLIC) {
                    mutableProp.setter.call(dto, value)
                }
            }
        }

        /**
         * Sets the values of properties in the specified object based on the keys and values in the given map.
         * @param dto The object for which to set the properties.
         * @param map A map containing the property names and corresponding values to set.
         *            The values can be of any type.
         */
        private fun mapIn(dto: Any, map: Map<String, *>) {
            for ((key, value) in map) {
                setProperty(dto, key, value)
            }
        }
    }
}