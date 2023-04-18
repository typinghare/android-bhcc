package csc244.note.common

import java.io.File
import java.io.FileNotFoundException

object LocalStorage {
    fun read(file: File): String {
        if (!file.isFile) {
            throw FileNotFoundException(file.absolutePath)
        }

        return file.readText()
    }

    fun write(file: File, content: String) {
        file.writeText(content)
    }
}