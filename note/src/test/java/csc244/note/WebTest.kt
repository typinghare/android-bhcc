package csc244.note

import csc244.note.common.web.Listener
import csc244.note.dto.UserDto
import org.junit.Test

class WebTest {
    @Test
    fun createListener() {
        val listener = Listener(UserDto::class) { userDto ->
            println(userDto.name)
        }

        val userDto = UserDto()
        userDto.name = "James Chan"

        listener.accept(userDto)
    }
}